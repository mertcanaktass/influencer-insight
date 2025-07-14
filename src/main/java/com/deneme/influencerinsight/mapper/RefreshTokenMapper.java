package com.deneme.influencerinsight.mapper;

import com.deneme.influencerinsight.dto.RefreshTokenDto;
import com.deneme.influencerinsight.model.RefreshTokenEntity;

public class RefreshTokenMapper {

    private RefreshTokenMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static RefreshTokenDto entityToRefreshTokenDto(RefreshTokenEntity entity) {
        if (entity == null) return null;
        return RefreshTokenDto.builder()
                .id(entity.getId())
                .token(entity.getToken())
                .expiryDate(entity.getExpiryDate())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .build();
    }

    public static RefreshTokenEntity refreshTokenDtoToEntity(RefreshTokenDto refreshTokenDto) {
        if (refreshTokenDto == null) return null;
        return RefreshTokenEntity.builder()
                .id(refreshTokenDto.getId())
                .token(refreshTokenDto.getToken())
                .expiryDate(refreshTokenDto.getExpiryDate())
                //.user(userEntity)
                .build();
    }
}
