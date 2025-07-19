package com.deneme.influencerinsight.mapper;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.enums.GeneralEnums;
import com.deneme.influencerinsight.model.RoleEntity;

public class RoleMapper {

    private RoleMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static RoleDto entityToRoleDto(RoleEntity entity) {
        if (entity == null) return null;
        return RoleDto.builder()
                .id(entity.getId())
                .roleType(entity.getRoleType())
                .status(GeneralEnums.Status.getStatusById(entity.getStatus()))
                .build();
    }

    public static RoleEntity roleDtoToEntity(RoleDto roleDto) {
        if (roleDto == null) return null;
        return RoleEntity.builder()
                .id(roleDto.getId())
                .roleType(roleDto.getRoleType())
                .status(GeneralEnums.Status.getIdByStatus(roleDto.getStatus()))
                .build();
    }
}
