package com.deneme.influencerinsight.mapper;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.dto.UserDto;
import com.deneme.influencerinsight.enums.Status;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.rest.responses.UserResponse;

import java.util.NoSuchElementException;
import java.util.Objects;

import static com.deneme.influencerinsight.mapper.RoleMapper.roleDtoToEntity;

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
                .status(userDto.getStatus().getId())
                .createDate(userDto.getCreateDate())
                .createUserId(Objects.nonNull(userDto.getCreateUserId()) ? userDto.getCreateUserId() : null)
                .updateUserId(Objects.nonNull(userDto.getUpdateUserId()) ? userDto.getUpdateUserId() : null)
                .updateDate(Objects.nonNull(userDto.getUpdateDate()) ? userDto.getUpdateDate() : null)
                .emailVerified(Boolean.TRUE.equals(userDto.getEmailVerified()))
                .verificationToken(userDto.getVerificationToken())
                .verificationTokenExpiresAt(userDto.getVerificationTokenExpiresAt())
                .roleEntity(roleDtoToEntity(roleDto))
                .build();

    }

    public static UserDto userEntityToUserDto(UserEntity userEntity) {
        if (Objects.isNull(userEntity)) throw new NoSuchElementException("User Not Found!");
        return UserDto.builder()
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .emailVerified(userEntity.isEmailVerified())
                .status(Status.getStatusById(userEntity.getStatus()))
                .createDate(userEntity.getCreateDate())
                .build();
    }

}
