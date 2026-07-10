package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.model.RefreshTokenEntity;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.repository.RefreshTokenRepository;
import com.deneme.influencerinsight.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final int TOKEN_BYTE_LENGTH = 32;

    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${auth.refresh-token.ttl-seconds:604800}")
    private long refreshTokenTtlSeconds;

    @Override
    @Transactional
    public String createRefreshToken(UserEntity user) {
        revokeTokensForUser(user);

        String token = generateToken();
        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(user)
                .tokenHash(hash(token))
                .expiryDate(Instant.now().plusSeconds(refreshTokenTtlSeconds))
                .build();
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Override
    @Transactional
    public UserEntity consumeRefreshToken(String token) {
        RefreshTokenEntity refreshToken = refreshTokenRepository.findByTokenHash(hash(token))
                .orElseThrow(() -> new AccessDeniedException("Invalid refresh token."));

        refreshTokenRepository.delete(refreshToken);

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new AccessDeniedException("Refresh token has expired. Please log in again.");
        }

        return refreshToken.getUser();
    }

    @Override
    @Transactional
    public void revokeTokensForUser(UserEntity user) {
        refreshTokenRepository.deleteByUser(user);
    }

    private String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable.", ex);
        }
    }

    @Scheduled(fixedDelayString = "${app.maintenance.cleanup-delay-ms:3600000}")
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
    }
}
