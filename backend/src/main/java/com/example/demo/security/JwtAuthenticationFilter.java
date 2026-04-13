package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * JWT Authentication Filter
 * Extracts Bearer token from Authorization header and validates it
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // Extract token from Authorization header
            String token = extractTokenFromRequest(request);

            if (token != null) {
                log.debug("Token found in Authorization header");
                
                // Validate token and get user
                User user = authService.getUserFromToken(token);

                if (user != null) {
                    log.debug("Token is valid for user: {}", user.getUsername());
                    
                    // Create granted authorities
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

                    // Create authentication token with authorities
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            null,
                            authorities
                        );
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("User authenticated: {}", user.getUsername());
                } else {
                    log.warn("Token validation failed: invalid token or user not found");
                }
            }
        } catch (Exception e) {
            log.error("Error processing authentication token: {}", e.getMessage());
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract Bearer token from Authorization header
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("Extracted token from Authorization header");
            return token;
        }

        return null;
    }
}
