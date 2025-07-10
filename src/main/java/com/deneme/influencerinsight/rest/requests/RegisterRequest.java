package com.deneme.influencerinsight.rest.requests;

import com.deneme.influencerinsight.dto.Role;
import com.deneme.influencerinsight.dto.User;
import lombok.Data;

@Data
public class RegisterRequest {

    private User user;
    private Role role;

}
