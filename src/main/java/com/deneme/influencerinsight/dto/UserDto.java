package com.deneme.influencerinsight.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class UserDto extends AbstractDto {
    private String username;
    private String password;
    private String email;
}
