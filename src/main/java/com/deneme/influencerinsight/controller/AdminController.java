package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.dto.requests.RegisterRequest;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.repository.RoleRepository;
import com.deneme.influencerinsight.repository.UserRepository;
import com.deneme.influencerinsight.security.JwtUtil;
import com.deneme.influencerinsight.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request) {
        try {
            userService.registerAdminUser(request);
            return ResponseEntity.ok("User created successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("User already exist!");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> userEntities = userService.getAllUsers();
        return ResponseEntity.ok(userEntities);
    }

}
