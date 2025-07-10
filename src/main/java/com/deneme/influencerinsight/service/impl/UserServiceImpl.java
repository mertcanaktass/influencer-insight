package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.config.SecurityConfig;
import com.deneme.influencerinsight.enums.RoleType;
import com.deneme.influencerinsight.model.RoleEntity;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.repository.UserRepository;
import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.service.RoleService;
import com.deneme.influencerinsight.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RoleService roleService;
    @Autowired
    private SecurityConfig securityConfig;

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public void registerAdminUser(RegisterRequest request) throws Exception {
        if (userRepository.existsByUsername(request.getUser().getUsername())) {
            throw new Exception("User Already Exist!");
        }

        RoleEntity adminRoleEntity = this.roleService.getRoleByType(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN rolü bulunamadı"));

        UserEntity userEntity = UserEntity.builder()
                .username(request.getUser().getUsername())
                .password(securityConfig.passwordEncoder().encode(request.getUser().getPassword()))
                .roleEntity(adminRoleEntity)
                .build();

        userRepository.save(userEntity);
    }
}
