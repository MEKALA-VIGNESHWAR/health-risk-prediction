package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AlertSystemDTO - Smart alerts and notifications for doctors and patients
 * Includes various alert types for clinical decision support
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertSystemDTO {
    private String alertId;
    private String patientId;
    private String patientName;
    private String alertType;  // HIGH_GLUCOSE, RISK_INCREASED, MISSED_FOLLOWUP, EMERGENCY, ABNORMAL_TREND
    private String severity;   // LOW, MEDIUM, HIGH, CRITICAL
    private String message;
    private String description;
    private Long timestamp;
    private Long createdDate;
    private Boolean acknowledged;
    private String acknowledgedBy;
    private Long acknowledgedDate;
    
    // Alert Specifics
    private Double previousValue;  // for trend-based alerts
    private Double currentValue;
    private String unit;  // mg/dL, %, etc.
    private String threshold;  // what threshold was exceeded
    
    // Recommendations Based on Alert
    private String recommendedAction;
    private String doctorNotes;
    
    // Status
    private String status;  // ACTIVE, RESOLVED, IGNORED
    
    // Alert Details
    private String category;  // CRITICAL, MEDICAL, ADMINISTRATIVE
    private Boolean requiresImmediateAction;
}
