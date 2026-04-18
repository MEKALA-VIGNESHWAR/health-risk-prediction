package com.example.demo.service;

import com.example.demo.entity.Alert;
import com.example.demo.entity.DiabetesPrediction;
import com.example.demo.repository.AlertRepositoryJPA;
import com.example.demo.repository.DiabetesPredictionRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * AlertService - Enhanced alert management using dedicated Alert entity
 * 
 * Features:
 * - Get alerts by user with severity filtering
 * - Alert statistics dashboard
 * - Bulk acknowledge/dismiss
 * - Alert generation from predictions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepositoryJPA alertRepository;
    private final DiabetesPredictionRepositoryJPA predictionRepository;

    /**
     * Get all unread alerts for a user
     */
    public List<Alert> getAlertsByUser(UUID userId) {
        return alertRepository.findByPatientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    /**
     * Get all alerts for a user (including read)
     */
    public List<Alert> getAllAlertsByUser(UUID userId) {
        return alertRepository.findByPatientIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get alerts filtered by severity
     */
    public List<Alert> getAlertsBySeverity(String severity) {
        return alertRepository.findBySeverityOrderByCreatedAtDesc(severity);
    }

    /**
     * Mark alert as acknowledged
     */
    @Transactional
    public void acknowledgeAlert(UUID alertId) {
        alertRepository.findById(alertId).ifPresent(alert -> {
            alert.setIsRead(true);
            alert.setAcknowledgedAt(LocalDateTime.now());
            alertRepository.save(alert);
            log.info("Alert {} acknowledged", alertId);
        });
    }

    /**
     * Acknowledge all alerts for a user
     */
    @Transactional
    public int acknowledgeAllAlerts(UUID userId) {
        return alertRepository.markAllReadForPatient(userId, LocalDateTime.now());
    }

    /**
     * Delete alert
     */
    @Transactional
    public void deleteAlert(UUID alertId) {
        alertRepository.deleteById(alertId);
        log.info("Alert {} deleted", alertId);
    }

    /**
     * Get alert statistics
     */
    public Map<String, Long> getAlertStatistics() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("total", alertRepository.countByIsReadFalse());
        stats.put("critical", alertRepository.countBySeverityAndIsReadFalse("CRITICAL"));
        stats.put("high", alertRepository.countBySeverityAndIsReadFalse("HIGH"));
        stats.put("medium", alertRepository.countBySeverityAndIsReadFalse("MEDIUM"));
        stats.put("low", alertRepository.countBySeverityAndIsReadFalse("LOW"));
        return stats;
    }

    /**
     * Get unread alert count for a user
     */
    public long getUnreadCount(UUID userId) {
        return alertRepository.countByPatientIdAndIsReadFalse(userId);
    }

    /**
     * Generate alerts from all recent predictions (batch job)
     */
    @Transactional
    public void generateAlertsFromPredictions() {
        log.info("Generating alerts from recent predictions");
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<DiabetesPrediction> recent = predictionRepository.findAll().stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(sevenDaysAgo))
                .toList();

        int generatedCount = 0;
        for (DiabetesPrediction pred : recent) {
            if (pred.getProbabilityDiabetes() != null && pred.getProbabilityDiabetes() > 0.7) {
                if (!alertRepository.existsByPredictionIdAndAlertType(pred.getId(), "RISK_THRESHOLD")) {
                    Alert alert = new Alert();
                    alert.setPatientId(pred.getUserId());
                    alert.setPredictionId(pred.getId());
                    alert.setAlertType("RISK_THRESHOLD");
                    alert.setSeverity(pred.getProbabilityDiabetes() > 0.9 ? "CRITICAL" : "HIGH");
                    alert.setTitle("High Risk Alert");
                    alert.setMessage(String.format("Patient has %.0f%% diabetes risk",
                            pred.getProbabilityDiabetes() * 100));
                    alert.setTriggerValue(pred.getProbabilityDiabetes() * 100);
                    alert.setThresholdValue(70.0);
                    alert.setTriggerMetric("risk");
                    alert.setIsRead(false);
                    alertRepository.save(alert);
                    generatedCount++;
                }
            }
        }
        log.info("Generated {} alerts from {} recent predictions", generatedCount, recent.size());
    }
}
