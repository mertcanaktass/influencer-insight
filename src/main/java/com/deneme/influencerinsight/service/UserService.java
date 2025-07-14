package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.dto.UserDto;
import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.rest.responses.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAllUsers();

    boolean existsByUsername(String username);

    void saveUser(UserDto userDto, RoleDto roleDto);

    void registerAdminUser(RegisterRequest request);

    void register(RegisterRequest registerRequest);

    Optional<UserDto> inquireUser(Long userId);
}
