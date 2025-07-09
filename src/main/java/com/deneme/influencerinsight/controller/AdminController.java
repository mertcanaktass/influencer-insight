package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.dto.requests.RegisterRequest;
import com.deneme.influencerinsight.dto.responses.RegisterResponse;
import com.deneme.influencerinsight.model.Role;
import com.deneme.influencerinsight.model.RoleType;
import com.deneme.influencerinsight.model.User;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AdminController(UserService userService,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerAdmin(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body(new RegisterResponse("Kullanıcı zaten mevcut!"));
        }

        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_ADMIN rolü bulunamadı"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(adminRole)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok(new RegisterResponse("Admin kullanıcı kaydedildi"));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

}
