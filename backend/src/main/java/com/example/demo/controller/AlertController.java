package com.example.demo.controller;

import com.example.demo.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AlertController - REST endpoints for alert management
 * 
 * Main endpoints:
 * - GET /api/alerts - Get all unacknowledged alerts
 * - GET /api/alerts/statistics - Get alert statistics
 * - PUT /api/alerts/{alertId}/acknowledge - Mark alert as read
 * - PUT /api/alerts/acknowledge-all - Mark all alerts as read
 * - DELETE /api/alerts/{alertId} - Delete alert
 */
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AlertController {

    private final AlertService alertService;

    /**
     * GET /api/alerts
     * Get all unacknowledged alerts
     * 
     * Query Parameters:
     * - severity: CRITICAL | HIGH | MEDIUM | LOW (optional)
     * 
     * Response:
     * {
     *   "status": "success",
     *   "data": [
     *     {
     *       "id": "uuid",
     *       "title": "Diabetes Risk Alert - CRITICAL",
     *       "message": "⚠️ CRITICAL: Blood glucose level is dangerously high...",
     *       "severity": "CRITICAL",
     *       "isRead": false,
     *       "timestamp": "2026-04-15T14:30:00",
     *       "patientId": "uuid",
     *       "patientName": "John Reddy"
     *     }
     *   ]
     * }
     */
    @GetMapping
    public ResponseEntity<?> getAlerts(
            @RequestParam(required = false) String severity) {
        
        try {
            log.info("GET /api/alerts - Severity filter: {}", severity);
            
            java.util.List<Map<String, Object>> alerts;
            
            if (severity != null && !severity.isBlank()) {
                alerts = alertService.getAlertsBySeverity(severity.toUpperCase());
            } else {
                // Get all unacknowledged alerts (empty list as we don't have user context here)
                alerts = java.util.List.of(); // Will be enhanced with user context
            }
            
            return ResponseEntity.ok(new ApiResponse("success", "Alerts retrieved", alerts));
        } catch (Exception e) {
            log.error("Error fetching alerts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to fetch alerts: " + e.getMessage(), null));
        }
    }

    /**
     * GET /api/alerts/statistics
     * Get alert statistics
     * 
     * Response:
     * {
     *   "status": "success",
     *   "data": {
     *     "total": 45,
     *     "critical": 8,
     *     "high": 15,
     *     "medium": 18,
     *     "low": 4
     *   }
     * }
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getAlertStatistics() {
        try {
            log.info("GET /api/alerts/statistics");
            Map<String, Integer> stats = alertService.getAlertStatistics();
            
            return ResponseEntity.ok(new ApiResponse("success", "Alert statistics retrieved", stats));
        } catch (Exception e) {
            log.error("Error fetching alert statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to fetch alert statistics: " + e.getMessage(), null));
        }
    }

    /**
     * PUT /api/alerts/{alertId}/acknowledge
     * Mark alert as read/acknowledged
     * 
     * Response:
     * {
     *   "status": "success",
     *   "message": "Alert acknowledged successfully"
     * }
     */
    @PutMapping("/{alertId}/acknowledge")
    public ResponseEntity<?> acknowledgeAlert(
            @PathVariable String alertId) {
        
        try {
            log.info("PUT /api/alerts/{}/acknowledge", alertId);
            
            UUID uuid = UUID.fromString(alertId);
            alertService.acknowledgeAlert(uuid);
            
            return ResponseEntity.ok(new ApiResponse("success", "Alert acknowledged successfully", null));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format: {}", alertId);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Invalid alert ID format", null));
        } catch (Exception e) {
            log.error("Error acknowledging alert: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to acknowledge alert: " + e.getMessage(), null));
        }
    }

    /**
     * PUT /api/alerts/acknowledge-all
     * Mark all alerts as read
     * 
     * Response:
     * {
     *   "status": "success",
     *   "message": "All alerts acknowledged successfully"
     * }
     */
    @PutMapping("/acknowledge-all")
    public ResponseEntity<?> acknowledgeAllAlerts(
            @RequestParam UUID userId) {
        
        try {
            log.info("PUT /api/alerts/acknowledge-all for user {}", userId);
            alertService.acknowledgeAllAlerts(userId);
            
            return ResponseEntity.ok(new ApiResponse("success", "All alerts acknowledged successfully", null));
        } catch (Exception e) {
            log.error("Error acknowledging all alerts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to acknowledge alerts: " + e.getMessage(), null));
        }
    }

    /**
     * DELETE /api/alerts/{alertId}
     * Delete alert
     * 
     * Response:
     * {
     *   "status": "success",
     *   "message": "Alert deleted successfully"
     * }
     */
    @DeleteMapping("/{alertId}")
    public ResponseEntity<?> deleteAlert(
            @PathVariable String alertId) {
        
        try {
            log.info("DELETE /api/alerts/{}", alertId);
            
            UUID uuid = UUID.fromString(alertId);
            alertService.deleteAlert(uuid);
            
            return ResponseEntity.ok(new ApiResponse("success", "Alert deleted successfully", null));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format: {}", alertId);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("error", "Invalid alert ID format", null));
        } catch (Exception e) {
            log.error("Error deleting alert: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to delete alert: " + e.getMessage(), null));
        }
    }

    /**
     * POST /api/alerts/trigger
     * Manually trigger alert generation from recent predictions
     * (Admin only)
     * 
     * Response:
     * {
     *   "status": "success",
     *   "message": "Alerts generated successfully"
     * }
     */
    @PostMapping("/trigger")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> triggerAlertGeneration() {
        try {
            log.info("POST /api/alerts/trigger");
            alertService.generateAlertsFromPredictions();
            
            return ResponseEntity.ok(new ApiResponse("success", "Alerts generated successfully", null));
        } catch (Exception e) {
            log.error("Error triggering alert generation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("error", "Failed to generate alerts: " + e.getMessage(), null));
        }
    }

    /**
     * ApiResponse wrapper class
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class ApiResponse {
        private String status;
        private String message;
        private Object data;
    }
}
