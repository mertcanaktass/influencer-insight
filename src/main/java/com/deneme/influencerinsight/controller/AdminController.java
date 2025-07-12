package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.rest.requests.RegisterAdminRequest;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.service.UserService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> registerAdmin(@RequestBody RegisterAdminRequest request) {
        try {
            userService.registerAdminUser(request);
            return ResponseEntity.ok("User created successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userList = userService.getAllUsers();
        return ResponseEntity.ok(userList);
    }

}
