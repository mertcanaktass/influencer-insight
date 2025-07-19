package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.dto.RefreshTokenDto;
import com.deneme.influencerinsight.mapper.RefreshTokenMapper;
import com.deneme.influencerinsight.model.RefreshTokenEntity;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.repository.RefreshTokenRepository;
import com.deneme.influencerinsight.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh.token.expiry}")
    private Long refreshTokenExpiry;

    @Override
    public RefreshTokenDto createRefreshToken(UserEntity user) {
        RefreshTokenEntity token = RefreshTokenEntity.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds(refreshTokenExpiry))
                .build();
        token = refreshTokenRepository.save(token);
        return RefreshTokenMapper.entityToRefreshTokenDto(token);
    }

    @Override
    public Optional<RefreshTokenDto> findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(RefreshTokenMapper::entityToRefreshTokenDto);
    }

    @Override
    public boolean isTokenExpired(RefreshTokenDto token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    @Override
    public void deleteByUser(UserEntity user) {
        refreshTokenRepository.deleteByUser(user);
    }
}

