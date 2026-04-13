package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController - REST endpoints for authentication (login, register)
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            log.info("Received registration request for username: {}", request.getUsername());
            
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Username is required", HttpStatus.BAD_REQUEST.value()));
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Password is required", HttpStatus.BAD_REQUEST.value()));
            }

            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Email is required", HttpStatus.BAD_REQUEST.value()));
            }

            LoginResponse response = authService.register(request);
            log.info("User registered successfully: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Registration failed: " + e.getMessage(), 
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Login user
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            log.info("Received login request for username: {}", request.getUsername());

            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Username is required", HttpStatus.BAD_REQUEST.value()));
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Password is required", HttpStatus.BAD_REQUEST.value()));
            }

            LoginResponse response = authService.login(request);
            log.info("User logged in successfully: {}", request.getUsername());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Login failed: " + e.getMessage(), 
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Verify token
     * GET /api/auth/verify
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Token is required", HttpStatus.UNAUTHORIZED.value()));
            }

            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            boolean isValid = authService.verifyToken(token);
            if (isValid) {
                return ResponseEntity.ok(new SuccessResponse("Token is valid", true));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid token", HttpStatus.UNAUTHORIZED.value()));
            }

        } catch (Exception e) {
            log.error("Token verification error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Verification failed: " + e.getMessage(), 
                            HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Error response class
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class ErrorResponse {
        private String error;
        private int status;
    }

    /**
     * Success response class
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class SuccessResponse {
        private String message;
        private boolean valid;
    }
}
