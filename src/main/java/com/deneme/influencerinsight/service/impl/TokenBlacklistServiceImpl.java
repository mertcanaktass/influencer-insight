package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.model.TokenBlacklistEntity;
import com.deneme.influencerinsight.repository.TokenBlacklistRepository;
import com.deneme.influencerinsight.service.TokenBlacklistService;
import com.deneme.influencerinsight.util.TokenHashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    public void blacklistToken(String token, Date expirationDate) {
        if (token == null || token.isEmpty()) {
            log.warn("Blacklist token failed: token is null or empty");
            return;
        }

        String tokenHash = TokenHashing.sha256(token);
        if (!tokenBlacklistRepository.existsByTokenHash(tokenHash)) {
            TokenBlacklistEntity entity = TokenBlacklistEntity.builder()
                    .tokenHash(tokenHash)
                    .expirationDate(expirationDate)
                    .build();
            tokenBlacklistRepository.save(entity);
            log.info("Access token blacklisted until {}", expirationDate);
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return tokenBlacklistRepository.existsByTokenHash(TokenHashing.sha256(token));
    }

    @Scheduled(fixedDelayString = "${app.maintenance.cleanup-delay-ms:3600000}")
    @Transactional
    public void deleteExpiredTokens() {
        tokenBlacklistRepository.deleteByExpirationDateBefore(new Date());
    }
}
