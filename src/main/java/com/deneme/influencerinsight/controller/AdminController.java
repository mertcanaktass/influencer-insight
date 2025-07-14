package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.dto.UserDto;
import com.deneme.influencerinsight.rest.requests.RegisterAdminRequest;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    @Autowired
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register Admin",
            description = "Register Admin API is designed for creating new Admin user, this API only works from Admin panel!",
            tags = "Admin User Management")
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterAdminRequest request) {
        try {
            userService.registerAdminUser(request);
            return ResponseEntity.ok("User created successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users")
    @Operation(summary = "Inquire All Users",
            description = "Get All Users API is inquires all users, only Admin users could use this API from Admin panel!.",
            tags = {"Admin User Management"})
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userList = userService.getAllUsers();
        return ResponseEntity.ok(userList);
    }

    @GetMapping("/inquireUser/{userId}")
    @Operation(summary = "Inquire User From User Id",
            description = "Inquire User API is used for inquire just one user from user id.",
            tags = {"User Management"})
    public ResponseEntity<?> inquireUser(Long userId) {
        try {
            Optional<UserDto> user = userService.inquireUser(userId);
            if (Objects.nonNull(user)) return ResponseEntity.ok(user);
            return ResponseEntity.notFound().build();
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().body(exception.getMessage());
        }

    }

}
