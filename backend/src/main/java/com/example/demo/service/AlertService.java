package com.example.demo.service;

import com.example.demo.dto.AlertSystemDTO;
import com.example.demo.entity.DiabetesPrediction;
import com.example.demo.entity.Notification;
import com.example.demo.repository.DiabetesPredictionRepositoryJPA;
import com.example.demo.repository.NotificationRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AlertService - Manages alerts for critical diabetes predictions
 * 
 * Features:
 * - Generate alerts from high-risk predictions
 * - Filter alerts by severity
 * - Mark alerts as acknowledged
 * - Delete alerts
 * - Get alert statistics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final DiabetesPredictionRepositoryJPA diabetesPredictionRepository;
    private final NotificationRepositoryJPA notificationRepository;

    /**
     * Generate alerts from all high-risk predictions
     * Alert thresholds:
     * - CRITICAL: Glucose >= 200 OR Risk >= 90%
     * - HIGH: Glucose >= 150 OR Risk >= 70%
     * - MEDIUM: Glucose >= 130 OR Risk >= 50%
     * - LOW: Risk >= 30%
     */
    @Transactional
    public void generateAlertsFromPredictions() {
        try {
            log.info("Generating alerts from predictions");
            
            // Get all predictions from last 7 days
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            List<DiabetesPrediction> recentPredictions = diabetesPredictionRepository
                    .findAll()
                    .stream()
                    .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(sevenDaysAgo))
                    .collect(Collectors.toList());
            
            for (DiabetesPrediction prediction : recentPredictions) {
                generateAlertForPrediction(prediction);
            }
            
            log.info("Alerts generated successfully");
        } catch (Exception e) {
            log.error("Error generating alerts: {}", e.getMessage());
        }
    }

    /**
     * Generate alert for a specific prediction if it meets alert criteria
     */
    @Transactional
    public void generateAlertForPrediction(DiabetesPrediction prediction) {
        try {
            String severity = determineSeverity(prediction);
            
            if (severity == null) {
                return; // No alert needed
            }
            
            String message = generateAlertMessage(prediction, severity);
            
            Notification notification = new Notification();
            notification.setUser(prediction.getUser());
            notification.setTitle("Diabetes Risk Alert - " + severity);
            notification.setMessage(message);
            notification.setSeverity(severity);
            notification.setRelatedPredictionId(prediction.getId());
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            
            notificationRepository.save(notification);
            
            log.debug("Alert created for prediction {} with severity {}", prediction.getId(), severity);
        } catch (Exception e) {
            log.error("Error generating alert for prediction: {}", e.getMessage());
        }
    }

    /**
     * Determine alert severity based on prediction metrics
     * Returns: CRITICAL | HIGH | MEDIUM | LOW | null
     */
    private String determineSeverity(DiabetesPrediction prediction) {
        double riskPercentage = prediction.getRiskPercentage() != null ? prediction.getRiskPercentage() : 0;
        double glucose = prediction.getGlucose() != null ? prediction.getGlucose() : 0;
        
        if (glucose >= 200 || riskPercentage >= 90) {
            return "CRITICAL";
        } else if (glucose >= 150 || riskPercentage >= 70) {
            return "HIGH";
        } else if (glucose >= 130 || riskPercentage >= 50) {
            return "MEDIUM";
        } else if (riskPercentage >= 30) {
            return "LOW";
        }
        
        return null; // No alert needed
    }

    /**
     * Generate alert message based on prediction details
     */
    private String generateAlertMessage(DiabetesPrediction prediction, String severity) {
        StringBuilder message = new StringBuilder();
        
        double glucose = prediction.getGlucose() != null ? prediction.getGlucose() : 0;
        double risk = prediction.getRiskPercentage() != null ? prediction.getRiskPercentage() : 0;
        
        switch (severity) {
            case "CRITICAL":
                message.append("⚠️ CRITICAL: ");
                if (glucose >= 200) {
                    message.append("Blood glucose level is dangerously high (").append(String.format("%.0f", glucose));
                    message.append("). Immediate medical attention recommended.");
                } else {
                    message.append("Very high diabetes risk (").append(String.format("%.1f", risk));
                    message.append("%). Please contact your doctor immediately.");
                }
                break;
            case "HIGH":
                message.append("🔴 HIGH RISK: ");
                if (glucose >= 150) {
                    message.append("Blood glucose level is elevated (").append(String.format("%.0f", glucose));
                    message.append("). Consider lifestyle adjustments and medication review.");
                } else {
                    message.append("High diabetes risk (").append(String.format("%.1f", risk));
                    message.append("%). Schedule a doctor's appointment soon.");
                }
                break;
            case "MEDIUM":
                message.append("🟡 MEDIUM ALERT: ");
                if (glucose >= 130) {
                    message.append("Blood glucose is slightly elevated (").append(String.format("%.0f", glucose));
                    message.append("). Monitor diet and exercise regularly.");
                } else {
                    message.append("Moderate diabetes risk (").append(String.format("%.1f", risk));
                    message.append("%). Maintain healthy lifestyle practices.");
                }
                break;
            case "LOW":
                message.append("ℹ️ INFO: ");
                message.append("Low diabetes risk (").append(String.format("%.1f", risk));
                message.append("%). Continue regular health monitoring.");
                break;
        }
        
        return message.toString();
    }

    /**
     * Get all unacknowledged alerts for a user
     */
    public List<Map<String, Object>> getAlertsByUser(UUID userId) {
        try {
            List<Notification> notifications = notificationRepository
                    .findAll()
                    .stream()
                    .filter(n -> n.getUser().getId().equals(userId) && !n.getIsRead())
                    .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                    .collect(Collectors.toList());
            
            return notifications.stream()
                    .map(this::convertToAlertMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching alerts for user: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get alerts filtered by severity
     */
    public List<Map<String, Object>> getAlertsBySeverity(String severity) {
        try {
            List<Notification> notifications = notificationRepository
                    .findAll()
                    .stream()
                    .filter(n -> n.getSeverity().equals(severity) && !n.getIsRead())
                    .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                    .collect(Collectors.toList());
            
            return notifications.stream()
                    .map(this::convertToAlertMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching alerts by severity: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Mark alert as acknowledged/read
     */
    @Transactional
    public void acknowledgeAlert(UUID alertId) {
        try {
            Optional<Notification> notification = notificationRepository.findById(alertId);
            if (notification.isPresent()) {
                notification.get().setIsRead(true);
                notificationRepository.save(notification.get());
                log.info("Alert {} marked as read", alertId);
            }
        } catch (Exception e) {
            log.error("Error acknowledging alert: {}", e.getMessage());
        }
    }

    /**
     * Acknowledge all alerts for a user
     */
    @Transactional
    public void acknowledgeAllAlerts(UUID userId) {
        try {
            List<Notification> notifications = notificationRepository
                    .findAll()
                    .stream()
                    .filter(n -> n.getUser().getId().equals(userId) && !n.getIsRead())
                    .collect(Collectors.toList());
            
            notifications.forEach(n -> n.setIsRead(true));
            notificationRepository.saveAll(notifications);
            
            log.info("All alerts for user {} marked as read", userId);
        } catch (Exception e) {
            log.error("Error acknowledging all alerts: {}", e.getMessage());
        }
    }

    /**
     * Delete alert
     */
    @Transactional
    public void deleteAlert(UUID alertId) {
        try {
            notificationRepository.deleteById(alertId);
            log.info("Alert {} deleted", alertId);
        } catch (Exception e) {
            log.error("Error deleting alert: {}", e.getMessage());
        }
    }

    /**
     * Get alert statistics
     */
    public Map<String, Integer> getAlertStatistics() {
        try {
            List<Notification> allAlerts = notificationRepository.findAll();
            
            Map<String, Integer> stats = new HashMap<>();
            stats.put("total", (int) allAlerts.stream().filter(n -> !n.getIsRead()).count());
            stats.put("critical", (int) allAlerts.stream()
                    .filter(n -> "CRITICAL".equals(n.getSeverity()) && !n.getIsRead()).count());
            stats.put("high", (int) allAlerts.stream()
                    .filter(n -> "HIGH".equals(n.getSeverity()) && !n.getIsRead()).count());
            stats.put("medium", (int) allAlerts.stream()
                    .filter(n -> "MEDIUM".equals(n.getSeverity()) && !n.getIsRead()).count());
            stats.put("low", (int) allAlerts.stream()
                    .filter(n -> "LOW".equals(n.getSeverity()) && !n.getIsRead()).count());
            
            return stats;
        } catch (Exception e) {
            log.error("Error getting alert statistics: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * Convert Notification to alert map for API response
     */
    private Map<String, Object> convertToAlertMap(Notification notification) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("id", notification.getId());
        alert.put("title", notification.getTitle());
        alert.put("message", notification.getMessage());
        alert.put("severity", notification.getSeverity());
        alert.put("isRead", notification.getIsRead());
        alert.put("timestamp", notification.getCreatedAt());
        alert.put("predictionId", notification.getRelatedPredictionId());
        
        // Add patient info if available
        if (notification.getUser() != null) {
            alert.put("patientId", notification.getUser().getId());
            alert.put("patientName", notification.getUser().getFirstName() + " " + notification.getUser().getLastName());
        }
        
        return alert;
    }
}
