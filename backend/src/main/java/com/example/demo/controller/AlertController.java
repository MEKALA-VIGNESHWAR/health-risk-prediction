package com.example.demo.controller;

import com.example.demo.entity.Alert;
import com.example.demo.repository.AlertRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AlertController - Industry-level REST endpoints for the smart alert system
 * 
 * Endpoints:
 * - GET  /api/alerts                    - All alerts (paginated)
 * - GET  /api/alerts/user/{userId}      - Alerts for a specific user
 * - GET  /api/alerts/count/{userId}     - Unread alert count
 * - GET  /api/alerts/statistics         - Alert statistics
 * - GET  /api/alerts/unread             - All unread alerts
 * - PUT  /api/alerts/{id}/acknowledge   - Mark alert as read
 * - PUT  /api/alerts/acknowledge-all    - Mark all as read for a user
 * - DELETE /api/alerts/{id}             - Delete an alert
 */
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AlertController {

    private final AlertRepositoryJPA alertRepository;

    /**
     * GET /api/alerts - Get all alerts with optional pagination and severity filter
     */
    @GetMapping
    public ResponseEntity<?> getAlerts(
            @RequestParam(required = false) String severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            log.info("GET /api/alerts - severity: {}, page: {}", severity, page);

            List<Alert> alerts;
            if (severity != null && !severity.isBlank()) {
                alerts = alertRepository.findBySeverityOrderByCreatedAtDesc(severity.toUpperCase());
            } else {
                Page<Alert> alertPage = alertRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
                alerts = alertPage.getContent();
            }

            return ResponseEntity.ok(new ApiResponse("success", "Alerts retrieved", alerts));
        } catch (Exception e) {
            log.error("Error fetching alerts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to fetch alerts: " + e.getMessage(), null));
        }
    }

    /**
     * GET /api/alerts/user/{userId} - Get all alerts for a specific patient
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAlertsForUser(@PathVariable String userId) {
        try {
            log.info("GET /api/alerts/user/{}", userId);
            UUID uuid = UUID.fromString(userId);

            List<Alert> alerts = alertRepository.findByPatientIdOrderByCreatedAtDesc(uuid);
            return ResponseEntity.ok(new ApiResponse("success", "User alerts retrieved", alerts));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Invalid user ID format", null));
        } catch (Exception e) {
            log.error("Error fetching user alerts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to fetch user alerts", null));
        }
    }

    /**
     * GET /api/alerts/count/{userId} - Get unread alert count for a user
     */
    @GetMapping("/count/{userId}")
    public ResponseEntity<?> getUnreadAlertCount(@PathVariable String userId) {
        try {
            log.info("GET /api/alerts/count/{}", userId);
            UUID uuid = UUID.fromString(userId);
            long count = alertRepository.countByPatientIdAndIsReadFalse(uuid);

            Map<String, Object> result = new HashMap<>();
            result.put("count", count);
            result.put("userId", userId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Invalid user ID format", null));
        } catch (Exception e) {
            log.error("Error fetching alert count: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to fetch alert count", null));
        }
    }

    /**
     * GET /api/alerts/unread - Get all unread alerts ordered by severity
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadAlerts() {
        try {
            log.info("GET /api/alerts/unread");
            List<Alert> alerts = alertRepository.findAllUnreadOrderBySeverity();
            return ResponseEntity.ok(new ApiResponse("success", "Unread alerts retrieved", alerts));
        } catch (Exception e) {
            log.error("Error fetching unread alerts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to fetch unread alerts", null));
        }
    }

    /**
     * GET /api/alerts/statistics - Get alert statistics breakdown
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getAlertStatistics() {
        try {
            log.info("GET /api/alerts/statistics");

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("total", alertRepository.countByIsReadFalse());
            stats.put("critical", alertRepository.countBySeverityAndIsReadFalse("CRITICAL"));
            stats.put("high", alertRepository.countBySeverityAndIsReadFalse("HIGH"));
            stats.put("medium", alertRepository.countBySeverityAndIsReadFalse("MEDIUM"));
            stats.put("low", alertRepository.countBySeverityAndIsReadFalse("LOW"));

            return ResponseEntity.ok(new ApiResponse("success", "Alert statistics retrieved", stats));
        } catch (Exception e) {
            log.error("Error fetching alert statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to fetch alert statistics", null));
        }
    }

    /**
     * PUT /api/alerts/{alertId}/acknowledge - Mark alert as read
     */
    @PutMapping("/{alertId}/acknowledge")
    public ResponseEntity<?> acknowledgeAlert(@PathVariable String alertId) {
        try {
            log.info("PUT /api/alerts/{}/acknowledge", alertId);

            UUID uuid = UUID.fromString(alertId);
            Optional<Alert> alertOpt = alertRepository.findById(uuid);
            if (alertOpt.isPresent()) {
                Alert alert = alertOpt.get();
                alert.setIsRead(true);
                alert.setAcknowledgedAt(LocalDateTime.now());
                alertRepository.save(alert);
                return ResponseEntity.ok(new ApiResponse("success", "Alert acknowledged", null));
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Invalid alert ID format", null));
        } catch (Exception e) {
            log.error("Error acknowledging alert: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to acknowledge alert", null));
        }
    }

    /**
     * PUT /api/alerts/acknowledge-all - Mark all alerts as read for a user
     */
    @PutMapping("/acknowledge-all")
    public ResponseEntity<?> acknowledgeAllAlerts(@RequestParam String userId) {
        try {
            log.info("PUT /api/alerts/acknowledge-all for user {}", userId);
            UUID uuid = UUID.fromString(userId);
            int updated = alertRepository.markAllReadForPatient(uuid, LocalDateTime.now());
            return ResponseEntity.ok(new ApiResponse("success",
                    updated + " alerts acknowledged", Map.of("count", updated)));
        } catch (Exception e) {
            log.error("Error acknowledging all alerts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to acknowledge alerts", null));
        }
    }

    /**
     * DELETE /api/alerts/{alertId} - Delete alert
     */
    @DeleteMapping("/{alertId}")
    public ResponseEntity<?> deleteAlert(@PathVariable String alertId) {
        try {
            log.info("DELETE /api/alerts/{}", alertId);
            UUID uuid = UUID.fromString(alertId);
            alertRepository.deleteById(uuid);
            return ResponseEntity.ok(new ApiResponse("success", "Alert deleted", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Invalid alert ID format", null));
        } catch (Exception e) {
            log.error("Error deleting alert: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to delete alert", null));
        }
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class ApiResponse {
        private String status;
        private String message;
        private Object data;
    }
}
