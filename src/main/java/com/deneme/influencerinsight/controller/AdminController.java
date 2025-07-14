package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.dto.UserDto;
import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register Admin",
            description = "Register Admin API is designed for creating new Admin user, this API only works from Admin panel!",
            tags = "Admin User Management")
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterRequest request) {
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
    public ResponseEntity<?> inquireUser(@PathVariable Long userId) {
        try {
            Optional<UserDto> user = userService.inquireUser(userId);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Kullanıcı bulunamadı");
        } catch (Exception exception) {
            return ResponseEntity.internalServerError().body("Sunucu hatası: " + exception.getMessage());
        }
    }

}
