package com.deneme.influencerinsight.rest.requests;

import com.deneme.influencerinsight.dto.UserDto;
import lombok.Data;

@Data
public class RegisterRequest {
    private UserDto userDto;
}
