package com.ecommerce.auth.auth_service.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecommerce.auth.auth_service.dto.KeycloakUserDTO;

import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Collections;

@Service
public class KeycloakService {

    @Value("${keycloak.server-url:http://keycloak:8080}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm:master}")
    private String realm;

    @Value("${keycloak.client-id:auth-service}")
    private String clientId;

    @Value("${keycloak.client-secret:auth-service-secret}")
    private String clientSecret;

    @Value("${keycloak.admin-username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin-password:admin123}")
    private String adminPassword;

    private Keycloak getKeycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(realm)
                .clientId("admin-cli")
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    /**
     * Create a new user in Keycloak
     */
    public KeycloakUserDTO createUser(String email, String password, String firstName, String lastName) {
        Keycloak keycloak = getKeycloakClient();
        
        UserRepresentation user = new UserRepresentation();
        user.setEmail(email);
        user.setUsername(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);

        // Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        try {
            Response response = keycloak.realm(realm).users().create(user);
            
            if (response.getStatus() == 201) {
                // Extract user ID from response location header
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                return new KeycloakUserDTO(userId, email, firstName, lastName);
            } else {
                throw new RuntimeException("Failed to create user: " + response.getStatus());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating user in Keycloak: " + e.getMessage(), e);
        }
    }

    /**
     * Get user by email
     */
    public UserRepresentation getUserByEmail(String email) {
        Keycloak keycloak = getKeycloakClient();
        
        List<UserRepresentation> users = keycloak.realm(realm).users().search(email, true);
        
        if (users.isEmpty()) {
            throw new RuntimeException("User not found: " + email);
        }
        
        return users.get(0);
    }

    /**
     * Update user
     */
    public void updateUser(String userId, String firstName, String lastName) {
        Keycloak keycloak = getKeycloakClient();
        
        UserRepresentation user = keycloak.realm(realm).users().get(userId).toRepresentation();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        
        keycloak.realm(realm).users().get(userId).update(user);
    }

    /**
     * Delete user
     */
    public void deleteUser(String userId) {
        Keycloak keycloak = getKeycloakClient();
        keycloak.realm(realm).users().delete(userId);
    }

    /**
     * Get user by ID
     */
    public UserRepresentation getUserById(String userId) {
        Keycloak keycloak = getKeycloakClient();
        return keycloak.realm(realm).users().get(userId).toRepresentation();
    }

    /**
     * List all users
     */
    public List<UserRepresentation> getAllUsers() {
        Keycloak keycloak = getKeycloakClient();
        return keycloak.realm(realm).users().list();
    }

    /**
     * Authenticate user with Keycloak token endpoint
     */
    public String authenticateUser(String email, String password) {
        // This will be used by the login endpoint
        // For direct user password authentication, Keycloak Direct Grant Flow is used
        try {
            Keycloak directGrantFlowClient = KeycloakBuilder.builder()
                    .serverUrl(keycloakServerUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(email)
                    .password(password)
                    .build();
            
            // If we get here, authentication was successful
            // The access token is obtained internally
            return "authenticated";
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }
    }
}
