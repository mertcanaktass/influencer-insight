package com.deneme.influencerinsight.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class UserDto extends AbstractDto {
    private String username;
    private String password;
    private String email;
    private Boolean emailVerified;
    private String verificationToken;
}
