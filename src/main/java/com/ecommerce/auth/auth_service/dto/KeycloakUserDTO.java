package com.ecommerce.auth.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakUserDTO {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
}
