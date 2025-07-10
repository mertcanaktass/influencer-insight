package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.config.SecurityConfig;
import com.deneme.influencerinsight.enums.RoleType;
import com.deneme.influencerinsight.model.RoleEntity;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.service.AuthService;
import com.deneme.influencerinsight.service.RoleService;
import com.deneme.influencerinsight.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private SecurityConfig securityConfig;


}
