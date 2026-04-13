package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * HealthController - Basic health check endpoints
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("Health check endpoint called");
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "RealTime application is running");
        response.put("database", "PostgreSQL via Supabase");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        log.info("Ping endpoint called");
        return ResponseEntity.ok("Pong");
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        log.info("Status endpoint called");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "RUNNING");
        response.put("timestamp", System.currentTimeMillis());
        response.put("application", "RealTime Diabetes Prediction");
        response.put("database", "Supabase PostgreSQL");
        return ResponseEntity.ok(response);
    }

}
