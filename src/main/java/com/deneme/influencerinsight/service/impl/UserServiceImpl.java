package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.dto.UserDto;
import com.deneme.influencerinsight.enums.RoleType;
import com.deneme.influencerinsight.enums.UserStatus;
import com.deneme.influencerinsight.exception.UserAlreadyExistsException;
import com.deneme.influencerinsight.exception.EmailVerificationRequiredException;
import com.deneme.influencerinsight.mapper.UserMapper;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.repository.UserRepository;
import com.deneme.influencerinsight.rest.requests.LoginRequest;
import com.deneme.influencerinsight.rest.requests.PasswordChangeRequest;
import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.rest.requests.TokenRefreshRequest;
import com.deneme.influencerinsight.rest.responses.JwtResponse;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.security.JwtUtil;
import com.deneme.influencerinsight.service.EmailService;
import com.deneme.influencerinsight.service.RefreshTokenService;
import com.deneme.influencerinsight.service.RoleService;
import com.deneme.influencerinsight.service.TokenBlacklistService;
import com.deneme.influencerinsight.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.email.verification-token-ttl-seconds:86400}")
    private long verificationTokenTtlSeconds;

    @Override
    public List<UserResponse> getAllUsersDto() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::userEntityToUserResponse)
                .toList();
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::userEntityToUserResponse)
                .toList();
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserEntity getRequiredUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    public UserEntity saveUser(UserDto userDto, RoleDto roleDto) {
        UserEntity userEntity = UserMapper.userDtoToEntity(
                userDto,
                passwordEncoder.encode(userDto.getPassword()),
                roleDto
        );
        return userRepository.save(userEntity);
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        if (existsByUsername(request.getUsername())
                || userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new UserAlreadyExistsException("Username or email address is already registered.");
        }

        String token = UUID.randomUUID().toString();

        UserDto userDto = UserDto.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .emailVerified(false)
                .verificationToken(token)
                .verificationTokenExpiresAt(Instant.now().plusSeconds(verificationTokenTtlSeconds))
                .accountStatus(UserStatus.PENDING_VERIFICATION)
                .createDate(new Date())
                .build();

        RoleDto roleDto = roleService.getRoleByType(RoleType.ROLE_CUSTOMER);

        emailService.sendVerificationEmail(userDto.getEmail(), token);
        UserEntity createdUser = saveUser(userDto, roleDto);
        return UserMapper.userEntityToUserResponse(createdUser);
    }

    @Override
    public UserResponse registerAdminUser(RegisterRequest request) {
        if (existsByUsername(request.getUsername())
                || userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new UserAlreadyExistsException("Username or email address is already registered.");
        }

        UserDto userDto = UserDto.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .emailVerified(true)
                .accountStatus(UserStatus.ACTIVE)
                .build();

        RoleDto adminRoleDto = roleService.getRoleByType(RoleType.ROLE_ADMIN);
        UserEntity createdUser = saveUser(userDto, adminRoleDto);
        return UserMapper.userEntityToUserResponse(createdUser);
    }

    @Override
    public JwtResponse login(LoginRequest request,
                             AuthenticationManager authenticationManager,
                             JwtUtil jwtUtil) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        if (!user.isEmailVerified()) {
            throw new EmailVerificationRequiredException();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public void logout(HttpServletRequest request, JwtUtil jwtUtil) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Date expirationDate = jwtUtil.getExpirationDate(token);
            tokenBlacklistService.blacklistToken(token, expirationDate);

            String username = jwtUtil.extractUsername(token);
            userRepository.findByUsername(username).ifPresent(refreshTokenService::revokeTokensForUser);
        }
    }

    @Override
    @Transactional
    public JwtResponse refreshToken(TokenRefreshRequest request,
                                    JwtUtil jwtUtil) {
        UserEntity user = refreshTokenService.consumeRefreshToken(request.getRefreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = refreshTokenService.createRefreshToken(user);
        return new JwtResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public Optional<UserResponse> inquireUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::userEntityToUserResponse);
    }

    @Override
    public Optional<UserResponse> inquireUserDto(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::userEntityToUserResponse);
    }

    @Override
    public Optional<UserResponse> inquireUserWithUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserMapper::userEntityToUserResponse);
    }

    @Override
    public void changePassword(String username, PasswordChangeRequest request) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new org.springframework.security.access.AccessDeniedException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        refreshTokenService.revokeTokensForUser(user);
    }

    @Override
    public void verifyUserEmail(String token) {
        UserEntity user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token."));

        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified.");
        }

        if (user.getVerificationTokenExpiresAt() == null
                || user.getVerificationTokenExpiresAt().isBefore(Instant.now())) {
            user.setVerificationToken(null);
            user.setVerificationTokenExpiresAt(null);
            userRepository.save(user);
            throw new IllegalArgumentException("Verification token has expired.");
        }

        user.setEmailVerified(true);
        user.setAccountStatus(UserStatus.ACTIVE);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiresAt(null);
        userRepository.save(user);
    }
}
