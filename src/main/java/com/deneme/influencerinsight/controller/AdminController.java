package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.enums.OperationType;
import com.deneme.influencerinsight.rest.requests.RegisterRequest;
import com.deneme.influencerinsight.rest.responses.AbstractResponse;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    public ResponseEntity<AbstractResponse<UserResponse>> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        UserResponse registeredUser = userService.registerAdminUser(request);
        AbstractResponse<UserResponse> response = new AbstractResponse<>();
        response.setOperationType(OperationType.CREATE_ADMIN_USER);
        if (Objects.nonNull(registeredUser)) {
            response.setResponseMessage("Admin User Registered Successfully!");
            response.setData(registeredUser);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setResponseMessage("Admin User Registration Failed!");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users")
    @Operation(
            summary = "Inquire All Users",
            description = "Get All Users API is inquires all users, only Admin users could use this API from Admin panel!",
            tags = {"Admin User Management"}
    )
    public ResponseEntity<AbstractResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> userList = userService.getAllUsersDto();
        AbstractResponse<List<UserResponse>> response = new AbstractResponse<>();
        response.setOperationType(OperationType.INQUIRE_USER);
        if (userList.isEmpty()) {
            response.setResponseMessage("Couldn't find any user!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            response.setData(userList);
            response.setResponseMessage(HttpStatus.OK.name());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("/inquireUser/{userId}")
    @Operation(
            summary = "Inquire User From User Id",
            description = "Inquire User API is used for inquire just one user from user id.",
            tags = {"User Management"}
    )
    public ResponseEntity<AbstractResponse<UserResponse>> inquireUser(@PathVariable Long userId) {
        Optional<UserResponse> userResponse = userService.inquireUserDto(userId);
        AbstractResponse<UserResponse> response = new AbstractResponse<>();
        response.setOperationType(OperationType.INQUIRE_USER);
        if (userResponse.isPresent()) {
            response.setData(userResponse.get());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setData(null);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
