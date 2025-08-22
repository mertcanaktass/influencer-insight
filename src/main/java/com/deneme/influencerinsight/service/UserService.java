package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.dto.UserDto;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.rest.requests.LoginRequest;
import com.deneme.influencerinsight.rest.requests.PasswordChangeRequest;
import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.rest.requests.TokenRefreshRequest;
import com.deneme.influencerinsight.rest.responses.JwtResponse;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAllUsersDto();

    List<UserResponse> getAllUsers();

    boolean existsByUsername(String username);

    UserEntity saveUser(UserDto userDto, RoleDto roleDto);

    UserResponse registerAdminUser(RegisterRequest request);

    UserResponse register(RegisterRequest registerRequest);

    JwtResponse login(LoginRequest request, AuthenticationManager authenticationManager, JwtUtil jwtUtil);

    void logout(HttpServletRequest request, JwtUtil jwtUtil);

    JwtResponse refreshToken(TokenRefreshRequest request, JwtUtil jwtUtil);

    Optional<UserResponse> inquireUser(Long userId);

    Optional<UserResponse> inquireUserDto(Long userId);

    Optional<UserResponse> inquireUserWithUsername(String username);

    void changePassword(String username, PasswordChangeRequest request);

    void verifyUserEmail(String token);
}
