package com.deneme.influencerinsight.rest.requests;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.dto.UserDto;
import lombok.Data;

@Data
public class RegisterAdminRequest {
    private UserDto userDto;
    private RoleDto roleDto;
    private String email;
}
