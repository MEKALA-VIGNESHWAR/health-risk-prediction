package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Doctor Dashboard DTOs for comprehensive patient management
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDashboardDTO {
    private DashboardSummary summary;
    private List<PatientRiskSummary> patientsAtRisk;
    private List<PredictionAlert> alerts;
    private PerformanceMetrics performanceMetrics;
    private SystemHealth systemHealth;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardSummary {
        private Integer totalPatients;
        private Integer totalPredictions;
        private Integer todayPredictions;
        private Integer patientsAtHighRisk;
        private Integer patientsAtMediumRisk;
        private Integer pendingReviews;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientRiskSummary {
        private String patientId;
        private String patientName;
        private String currentRiskLevel;
        private Double currentProbability;
        private String lastPredictionDate;
        private Integer predictionCount;
        private String trendDirection;  // UP, DOWN, STABLE
        private String status;  // PENDING, REVIEWED, NEEDS_ATTENTION
        private String doctorNotes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PredictionAlert {
        private String alertId;
        private String patientId;
        private String patientName;
        private String alertType;  // HIGH_RISK, CONFIDENCE_LOW, BORDERLINE, TREND_UP
        private String severity;   // LOW, MEDIUM, HIGH, CRITICAL
        private String message;
        private Long timestamp;
        private Boolean acknowledged;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceMetrics {
        private Double averageAccuracy;
        private Double averagePrecision;
        private Double averageRecall;
        private Double averageF1Score;
        private Double averageROC_AUC;
        private Integer totalModelsCompared;
        private String bestPerformingModel;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemHealth {
        private String modelStatus;  // HEALTHY, NEEDS_RETRAINING, DEPRECATED
        private String lastModelUpdate;
        private Double modelAccuracy;
        private Integer daysUntilRetrain;
        private String dataQuality;  // GOOD, ACCEPTABLE, POOR
        private Integer dataRecordsProcessed;
    }
}
