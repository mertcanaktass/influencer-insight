package com.deneme.influencerinsight.mapper;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.dto.UserDto;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.rest.responses.UserResponse;

import static com.deneme.influencerinsight.mapper.RoleMapper.roleToEntity;

public class UserMapper {

    public static UserResponse entityToUserResponse(UserEntity entity) {
        if (entity == null) return null;
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .build();
    }

    public static UserEntity userDtoToEntity(UserDto dto, String hashedPassword, RoleDto roleDto) {
        if (dto == null) return null;
        return UserEntity.builder()
                .username(dto.getUsername())
                .password(hashedPassword != null ? hashedPassword : dto.getPassword())
                .email(dto.getEmail())
                .roleEntity(roleToEntity(roleDto))
                .build();
    }
}
