package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.dto.UserDto;
import com.deneme.influencerinsight.enums.RoleType;
import com.deneme.influencerinsight.enums.Status;
import com.deneme.influencerinsight.exception.UserAlreadyExistsException;
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
import com.deneme.influencerinsight.service.RoleService;
import com.deneme.influencerinsight.service.TokenBlacklistService;
import com.deneme.influencerinsight.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

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
    public void saveUser(UserDto userDto, RoleDto roleDto) {
        UserEntity userEntity = UserMapper.userDtoToEntity(
                userDto,
                passwordEncoder.encode(userDto.getPassword()),
                roleDto
        );
        userRepository.save(userEntity);
    }

    @Override
    public void register(RegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        String token = UUID.randomUUID().toString();

        UserDto userDto = UserDto.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .emailVerified(false)
                .verificationToken(token)
                .status(Status.getStatusByShortCode("passive")) // Kullanıcı kayıt olduğunda statu 0 (pasif)
                .createDate(new Date())
                .build();

        RoleDto roleDto = roleService.getRoleByType(RoleType.ROLE_CUSTOMER);

        emailService.sendVerificationEmail(userDto.getEmail(), token);
        saveUser(userDto, roleDto);
    }

    @Override
    public void registerAdminUser(RegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        UserDto userDto = UserDto.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .emailVerified(true)
                .build();

        RoleDto adminRoleDto = roleService.getRoleByType(RoleType.ROLE_ADMIN);
        saveUser(userDto, adminRoleDto);
    }

    @Override
    public JwtResponse login(LoginRequest request,
                             AuthenticationManager authenticationManager,
                             JwtUtil jwtUtil) {

        UserDto userDto = userRepository.findByUsername(request.getUsername())
                .map(UserMapper::userEntityToUserDto)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if (Boolean.FALSE.equals(userDto.getEmailVerified())) {
            throw new AccessDeniedException("You need to verify your email address.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public void logout(HttpServletRequest request, JwtUtil jwtUtil) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Date expirationDate = jwtUtil.getExpirationDate(token);
            tokenBlacklistService.blacklistToken(token, expirationDate);
        }
    }

    @Override
    public JwtResponse refreshToken(TokenRefreshRequest request,
                                    JwtUtil jwtUtil) {
        String username = jwtUtil.extractUsername(request.getRefreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtUtil.isTokenExpired(request.getRefreshToken())) {
            String newAccessToken = jwtUtil.generateAccessToken(userDetails);
            return new JwtResponse(newAccessToken, request.getRefreshToken());
        } else {
            throw new AccessDeniedException("Refresh token has expired. Please login again.");
        }
    }

    @Override
    public Optional<UserResponse> inquireUser(Long userId) {
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
            throw new AccessDeniedException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdateDate(new Date());
        userRepository.save(user);
    }

    @Override
    public void verifyUserEmail(String token) {
        UserEntity user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token."));

        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified.");
        }

        user.setEmailVerified(true);
        user.setStatus(1); // Kullanıcı eğer email doğrulamasını başarılı şekilde yaptıysa statu 1'e (aktif) alınır.
        user.setVerificationToken(null);
        userRepository.save(user);
    }
}
