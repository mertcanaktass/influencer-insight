package com.deneme.influencerinsight.mapper;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.model.RoleEntity;

public class RoleMapper {

    public static RoleDto entityToRole(RoleEntity entity) {
        if (entity == null) return null;
        return RoleDto.builder()
                .id(entity.getId())
                .roleType(entity.getRoleType())
                .status(entity.getStatus())
                .build();
    }

    public static RoleEntity roleToEntity(RoleDto dto) {
        if (dto == null) return null;
        return RoleEntity.builder()
                .id(dto.getId())
                .roleType(dto.getRoleType())
                .status(dto.getStatus())
                .build();
    }
}
