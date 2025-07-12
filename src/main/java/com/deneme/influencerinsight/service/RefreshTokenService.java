package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.dto.RefreshTokenDto;
import com.deneme.influencerinsight.model.UserEntity;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshTokenDto createRefreshToken(UserEntity user);

    Optional<RefreshTokenDto> findByToken(String token);

    boolean isTokenExpired(RefreshTokenDto token);

    void deleteByUser(UserEntity user);
}
