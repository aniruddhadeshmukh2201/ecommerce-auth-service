package com.ecommerce.auth.auth_service.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.auth.auth_service.dto.AuthResponse;
import com.ecommerce.auth.auth_service.dto.LoginRequest;
import com.ecommerce.auth.auth_service.dto.SignupRequest;
import com.ecommerce.auth.auth_service.dto.UpdateUserRequest;
import com.ecommerce.auth.auth_service.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user
     */
    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    /**
     * Login user
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * Get user profile
     */
    @GetMapping("/profile/{userId}")
    public AuthResponse getUserProfile(@PathVariable String userId) {
        return authService.getUserProfile(userId);
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile/{userId}")
    public AuthResponse updateUserProfile(
            @PathVariable String userId,
            @RequestBody UpdateUserRequest request) {
        return authService.updateUserProfile(userId, request.getFirstName(), request.getLastName());
    }

    /**
     * Delete user account
     */
    @DeleteMapping("/profile/{userId}")
    public AuthResponse deleteUser(@PathVariable String userId) {
        return authService.deleteUser(userId);
    }
}
