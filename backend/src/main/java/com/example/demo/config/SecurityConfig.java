package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Security Configuration for REST API
 * Allows public access to key endpoints, secures others
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Allow static resources without authentication
                .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/images/**", "/public/**", "/static/**", 
                                "/*.css", "/*.js", "/*.ico", "/*.png", "/*.jpg", "/*.jpeg", "/*.svg", "/*.woff", 
                                "/*.woff2", "/*.ttf", "/*.eot").permitAll()
                
                // Public API endpoints - no authentication required
                .requestMatchers("/api/health", "/api/ping", "/api/status").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/verify").permitAll()
                .requestMatchers("/api/predict/diabetes").permitAll()
                .requestMatchers("/api/predict/heart").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                
                // Require authentication for other API endpoints
                .requestMatchers("/api/**").authenticated()
                
                // Allow other requests
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(basic -> basic.disable())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
            .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
