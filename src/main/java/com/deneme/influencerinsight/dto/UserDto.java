package com.deneme.influencerinsight.dto;

import com.deneme.influencerinsight.model.RoleEntity;
import com.deneme.influencerinsight.enums.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class UserDto extends AbstractDto {
    private String username;
    private String password;
    private String email;
    private Boolean emailVerified;
    private UserStatus accountStatus;
    private String verificationToken;
    private Instant verificationTokenExpiresAt;
    private RoleEntity roleEntity;
}
