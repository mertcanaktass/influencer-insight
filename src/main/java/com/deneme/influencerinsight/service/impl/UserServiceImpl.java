package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.config.SecurityConfig;
import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.dto.UserDto;
import com.deneme.influencerinsight.enums.RoleType;
import com.deneme.influencerinsight.mapper.UserMapper;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.repository.UserRepository;
import com.deneme.influencerinsight.rest.requests.RegisterAdminRequest;
import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.service.RoleService;
import com.deneme.influencerinsight.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SecurityConfig securityConfig;
    private final RoleService roleService;

    public UserServiceImpl(UserRepository userRepository,
                           SecurityConfig securityConfig,
                           RoleService roleService) {
        this.userRepository = userRepository;
        this.securityConfig = securityConfig;
        this.roleService = roleService;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        return userEntities.stream()
                .map(UserMapper::entityToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void saveUser(UserDto userDto, RoleDto roleDto) {
        UserEntity userEntity = UserMapper.userDtoToEntity(userDto, securityConfig.passwordEncoder().encode(userDto.getPassword()), roleDto);
        userRepository.save(userEntity);
    }

    @Override
    public void register(RegisterRequest registerRequest) throws Exception {
        UserDto userDto = registerRequest.getUserDto();
        if (existsByUsername(userDto.getUsername())) {
            throw new Exception("User already exists!");
        }
        RoleDto roleDto = roleService.getRoleByType(RoleType.ROLE_USER);

        saveUser(userDto, roleDto);
    }

    @Override
    public void registerAdminUser(RegisterAdminRequest request) throws Exception {
        UserDto userDto = request.getUserDto();
        if (existsByUsername(userDto.getUsername())) {
            throw new Exception("User already exists!");
        }
        RoleDto adminRoleDtoEntity = this.roleService.getRoleByType(RoleType.ROLE_ADMIN);

        saveUser(userDto, adminRoleDtoEntity);
    }
}
