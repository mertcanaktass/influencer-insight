package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.rest.requests.LoginRequest;
import com.deneme.influencerinsight.rest.requests.PasswordChangeRequest;
import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.rest.requests.TokenRefreshRequest;
import com.deneme.influencerinsight.rest.responses.JwtResponse;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.security.JwtUtil;
import com.deneme.influencerinsight.service.TokenBlacklistService;
import com.deneme.influencerinsight.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;

    @PreAuthorize("permitAll()")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        try {
            this.userService.register(request);
            return ResponseEntity.ok("User registered!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        JwtResponse jwtResponse = userService.login(request, authenticationManager, jwtUtil);
        return ResponseEntity.ok(jwtResponse);
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        try {
            JwtResponse response = userService.refreshToken(request, jwtUtil);
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JwtResponse(null, null));
        }
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        userService.logout(request, jwtUtil);
        return ResponseEntity.ok("Logout successful.");
    }

    @PreAuthorize("authentication.name == #username")
    @GetMapping("/profile/{username}")
    public ResponseEntity<UserResponse> getProfile(@PathVariable String username) {
        Optional<UserResponse> user = userService.inquireUserWithUsername(username);
        user.ifPresent(ResponseEntity::ok);
        throw new RuntimeException("User not found!");
    }

    @PreAuthorize("permitAll()")
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody PasswordChangeRequest request,
            Principal principal) {
        userService.changePassword(principal.getName(), request);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        try {
            userService.verifyUserEmail(token);
            return ResponseEntity.ok("Email verified successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Verification failed: " + e.getMessage());
        }
    }

}
