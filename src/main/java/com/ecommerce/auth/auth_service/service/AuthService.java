package com.ecommerce.auth.auth_service.service;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import com.ecommerce.auth.auth_service.dto.AuthResponse;
import com.ecommerce.auth.auth_service.dto.KeycloakUserDTO;
import com.ecommerce.auth.auth_service.dto.LoginRequest;
import com.ecommerce.auth.auth_service.dto.SignupRequest;

@Service
public class AuthService {

    private final KeycloakService keycloakService;

    public AuthService(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }

    /**
     * Register a new user in Keycloak
     */
    public AuthResponse signup(SignupRequest request) {
        try {
            // Validate input
            if (request.getEmail() == null || request.getPassword() == null) {
                return new AuthResponse(null, null, null, null, 
                    "Email and password are required", false);
            }

            // Create user in Keycloak
            KeycloakUserDTO user = keycloakService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName()
            );

            return new AuthResponse(
                user.getUserId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                "User registered successfully",
                true
            );
        } catch (Exception e) {
            return new AuthResponse(null, null, null, null,
                "Signup failed: " + e.getMessage(), false);
        }
    }

    /**
     * Authenticate user with Keycloak
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // Validate input
            if (request.getEmail() == null || request.getPassword() == null) {
                return new AuthResponse(null, null, null, null,
                    "Email and password are required", false);
            }

            // Authenticate user with Keycloak
            keycloakService.authenticateUser(request.getEmail(), request.getPassword());

            // Get user details from Keycloak
            UserRepresentation user = keycloakService.getUserByEmail(request.getEmail());

            return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                "Login successful",
                true
            );
        } catch (Exception e) {
            return new AuthResponse(null, null, null, null,
                "Login failed: Invalid credentials", false);
        }
    }

    /**
     * Get user profile
     */
    public AuthResponse getUserProfile(String userId) {
        try {
            UserRepresentation user = keycloakService.getUserById(userId);
            
            return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                "User retrieved successfully",
                true
            );
        } catch (Exception e) {
            return new AuthResponse(null, null, null, null,
                "Failed to retrieve user: " + e.getMessage(), false);
        }
    }

    /**
     * Update user profile
     */
    public AuthResponse updateUserProfile(String userId, String firstName, String lastName) {
        try {
            keycloakService.updateUser(userId, firstName, lastName);
            
            UserRepresentation user = keycloakService.getUserById(userId);
            
            return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                "User updated successfully",
                true
            );
        } catch (Exception e) {
            return new AuthResponse(null, null, null, null,
                "Failed to update user: " + e.getMessage(), false);
        }
    }

    /**
     * Delete user account
     */
    public AuthResponse deleteUser(String userId) {
        try {
            keycloakService.deleteUser(userId);
            
            return new AuthResponse(null, null, null, null,
                "User deleted successfully", true);
        } catch (Exception e) {
            return new AuthResponse(null, null, null, null,
                "Failed to delete user: " + e.getMessage(), false);
        }
    }
}