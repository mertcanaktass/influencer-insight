package com.deneme.influencerinsight.mapper;

import com.deneme.influencerinsight.dto.RefreshTokenDto;
import com.deneme.influencerinsight.model.RefreshTokenEntity;
import com.deneme.influencerinsight.model.UserEntity;

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

    public static RefreshTokenEntity refreshTokenDtoToEntity(RefreshTokenDto dto) {
        if (dto == null) return null;
        return RefreshTokenEntity.builder()
                .id(dto.getId())
                .token(dto.getToken())
                .expiryDate(dto.getExpiryDate())
                .build();
    }

    public static RefreshTokenEntity refreshTokenDtoToEntityWithUser(RefreshTokenDto dto, UserEntity userEntity) {
        if (dto == null) return null;
        return RefreshTokenEntity.builder()
                .id(dto.getId())
                .token(dto.getToken())
                .expiryDate(dto.getExpiryDate())
                .user(userEntity)
                .build();
    }
}
