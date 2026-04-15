package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * PatientHealthDetailsDTO - Comprehensive patient health information
 * Contains medical history, current status, and monitoring data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientHealthDetailsDTO {
    private String patientId;
    private String patientName;
    private String email;
    private Integer age;
    private String gender;  // MALE, FEMALE, OTHER
    private String bloodType;
    
    // Current Health Metrics
    private Double currentGlucose;
    private String bloodPressure;  // e.g., "120/80"
    private Double currentBMI;
    private Double currentInsulin;
    private Double skinThickness;
    private Double diabetesPedigreeFunction;
    
    // Medical History
    private Boolean hasFamilyHistory;  // family history of diabetes
    private Boolean hasHypertension;
    private Boolean hasHighCholesterol;
    private String medications;  // comma-separated list
    private String allergies;
    private String chronicConditions;
    
    // Health Status
    private String currentDiabetesStatus;  // NOT_DIABETIC, PRE_DIABETIC, DIABETIC
    private Double riskPercentage;
    private String riskLevel;  // LOW, MEDIUM, HIGH, CRITICAL
    
    // Timestamps
    private Long lastCheckupDate;
    private Long lastUpdatedDate;
    private Long createdDate;
    
    // Doctor Notes
    private String doctorNotes;
    private String reviewedBy;  // doctor name
    
    // Recent Predictions
    private List<RecentPrediction> recentPredictions;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentPrediction {
        private String predictionId;
        private Double probability;
        private String riskLevel;
        private Long predictionDate;
        private String status;
    }
}
