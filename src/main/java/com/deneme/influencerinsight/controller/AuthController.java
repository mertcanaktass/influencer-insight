package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.enums.OperationType;
import com.deneme.influencerinsight.rest.requests.LoginRequest;
import com.deneme.influencerinsight.rest.requests.PasswordChangeRequest;
import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.rest.requests.TokenRefreshRequest;
import com.deneme.influencerinsight.rest.responses.AbstractResponse;
import com.deneme.influencerinsight.rest.responses.JwtResponse;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.security.JwtUtil;
import com.deneme.influencerinsight.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AbstractResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        UserResponse registeredUser = userService.register(request);
        AbstractResponse<UserResponse> response = new AbstractResponse<>();
        response.setOperationType(OperationType.CREATE_CUSTOMER_USER);
        if (Objects.nonNull(registeredUser)) {
            response.setResponseMessage("User Registered Successfully!");
            response.setData(registeredUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setResponseMessage("User Registration Failed!");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        JwtResponse jwtResponse = userService.login(request, authenticationManager, jwtUtil);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        JwtResponse response = userService.refreshToken(request, jwtUtil);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        userService.logout(request, jwtUtil);
        return ResponseEntity.ok("Logout successful.");
    }

    @PreAuthorize("authentication.name == #username")
    @GetMapping("/profile/{username}")
    public ResponseEntity<UserResponse> getProfile(@PathVariable String username) {
        return userService.inquireUserWithUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequest request, Principal principal) {
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Password updated successfully");
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        userService.verifyUserEmail(token);
        return ResponseEntity.ok("Email verified successfully!");
    }
}
