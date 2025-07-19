package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(
            summary = "Register Admin",
            description = "Register Admin API is designed for creating new Admin user, this API only works from Admin panel!",
            tags = "Admin User Management"
    )
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterRequest request) {
        userService.registerAdminUser(request);
        return ResponseEntity.ok("User created successfully!");
    }

    @GetMapping("/users")
    @Operation(
            summary = "Inquire All Users",
            description = "Get All Users API is inquires all users, only Admin users could use this API from Admin panel!",
            tags = {"Admin User Management"}
    )
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userList = userService.getAllUsers();
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/inquireUser/{userId}")
    @Operation(
            summary = "Inquire User From User Id",
            description = "Inquire User API is used for inquire just one user from user id.",
            tags = {"User Management"}
    )
    public ResponseEntity<UserResponse> inquireUser(@PathVariable Long userId) {
        return userService.inquireUser(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }
}
