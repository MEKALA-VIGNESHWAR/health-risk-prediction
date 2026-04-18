package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Alert Entity - Industry-level alert system for health risk monitoring
 * 
 * Triggers:
 * - risk > 70%
 * - glucose > 180
 * - BMI > 30
 * - BP > 140
 */
@Entity
@Table(name = "alerts", indexes = {
    @Index(name = "idx_alerts_patient_id", columnList = "patient_id"),
    @Index(name = "idx_alerts_severity", columnList = "severity"),
    @Index(name = "idx_alerts_is_read", columnList = "is_read"),
    @Index(name = "idx_alerts_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "doctor_id")
    private UUID doctorId;

    @Column(name = "prediction_id")
    private UUID predictionId;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    /** CRITICAL, HIGH, MEDIUM, LOW */
    @Column(nullable = false, length = 20)
    private String severity;

    /** RISK_THRESHOLD, GLUCOSE_HIGH, BMI_HIGH, BP_HIGH, SYSTEM */
    @Column(name = "alert_type", nullable = false, length = 50)
    private String alertType;

    /** The metric value that triggered the alert */
    @Column(name = "trigger_value")
    private Double triggerValue;

    /** The threshold that was exceeded */
    @Column(name = "threshold_value")
    private Double thresholdValue;

    /** The metric name (risk, glucose, bmi, bp) */
    @Column(name = "trigger_metric", length = 50)
    private String triggerMetric;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "acknowledged_by", length = 255)
    private String acknowledgedBy;

    /** Patient name (denormalized for fast queries) */
    @Column(name = "patient_name", length = 255)
    private String patientName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (isRead == null) {
            isRead = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
