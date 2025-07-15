package com.deneme.influencerinsight.mapper;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.dto.UserDto;
import com.deneme.influencerinsight.enums.GeneralEnums;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.rest.responses.UserResponse;

import java.util.NoSuchElementException;
import java.util.Objects;

import static com.deneme.influencerinsight.mapper.RoleMapper.roleToEntity;

public class UserMapper {

    private UserMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static UserResponse userEntityToUserResponse(UserEntity entity) {
        if (entity == null) return null;
        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .build();
    }

    public static UserEntity userDtoToEntity(UserDto userDto, String hashedPassword, RoleDto roleDto) {
        if (userDto == null) return null;
        return UserEntity.builder()
                .username(userDto.getUsername())
                .password(hashedPassword != null ? hashedPassword : userDto.getPassword())
                .email(userDto.getEmail())
                .emailVerified(Boolean.TRUE.equals(userDto.getEmailVerified()))
                .verificationToken(userDto.getVerificationToken())
                .roleEntity(roleToEntity(roleDto))
                .build();

    }

    public static UserDto userEntityToUserDto(UserEntity userEntity) {
        if (Objects.isNull(userEntity)) throw new NoSuchElementException("User Not Found!");
        return UserDto.builder()
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .status(GeneralEnums.Status.getStatusById(userEntity.getStatus()))
                .createDate(userEntity.getCreateDate())
                .build();
    }
}
