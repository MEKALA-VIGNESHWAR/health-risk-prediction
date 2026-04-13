package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * AuthService - Authentication and authorization business logic (Supabase PostgreSQL)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepositoryJPA userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     */
    public LoginResponse register(RegisterRequest request) {
        log.info("Registering new user with username: {}", request.getUsername());

        // Validate input
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            log.warn("Username is empty");
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            log.warn("Password is empty");
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Passwords do not match");
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Validate password strength (minimum 6 characters)
        if (request.getPassword().length() < 6) {
            log.warn("Password too short");
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        // Create new user with hashed password
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password with BCrypt
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setCreatedDate(System.currentTimeMillis());
        user.setUpdatedDate(System.currentTimeMillis());

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        // Generate token
        String token = generateToken(savedUser.getId().toString());

        return new LoginResponse(
                savedUser.getId().toString(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                token,
                "Registration successful"
        );
    }

    /**
     * Login user with username and password
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        if (request.getUsername() == null || request.getPassword() == null) {
            log.warn("Username or password is null");
            throw new IllegalArgumentException("Username and password are required");
        }

        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isEmpty()) {
            log.warn("User not found: {}", request.getUsername());
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userOptional.get();

        // Validate password using BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password for user: {}", request.getUsername());
            throw new IllegalArgumentException("Invalid username or password");
        }

        log.info("User logged in successfully: {}", user.getUsername());

        // Generate token
        String token = generateToken(user.getId().toString());

        return new LoginResponse(
                user.getId().toString(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                token,
                "Login successful"
        );
    }

    /**
     * Verify user by token (simple UUID-based token)
     */
    public boolean verifyToken(String token) {
        // In production, use JWT library to verify token
        return token != null && !token.isEmpty();
    }

    /**
     * Generate authentication token (simple UUID, replace with JWT in production)
     */
    private String generateToken(String userId) {
        return "TOKEN_" + userId + "_" + UUID.randomUUID().toString();
    }

    /**
     * Get user by token
     */
    public User getUserFromToken(String token) {
        if (token == null || !token.startsWith("TOKEN_")) {
            return null;
        }

        try {
            // Extract userId from token (format: TOKEN_userId_uuid)
            String[] parts = token.split("_", 3);
            if (parts.length >= 2) {
                String userIdStr = parts[1];
                try {
                    UUID userId = UUID.fromString(userIdStr);
                    Optional<User> user = userRepository.findById(userId);
                    return user.orElse(null);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid UUID format in token: {}", userIdStr);
                    return null;
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse token: {}", e.getMessage());
        }

        return null;
    }
}

