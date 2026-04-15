package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LoginResponse DTO - returns user info and session token on successful login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String token;
    private String role;
    private String message;
}
