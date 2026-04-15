package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * EnhancedDashboardDTO - Comprehensive dashboard with all analytics and stats
 * Combines all dashboard components into one response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedDashboardDTO {
    
    // ===== SUMMARY CARDS STATS =====
    private DashboardStats stats;
    
    // ===== CHARTS DATA =====
    private ChartsData chartsData;
    
    // ===== ALERTS & NOTIFICATIONS =====
    private List<AlertSystemDTO> recentAlerts;
    private Integer alertCount;
    
    // ===== CRITICAL PATIENTS =====
    private List<PatientRiskSummary> criticalPatients;
    
    // ===== PENDING ACTIONS =====
    private List<PendingAction> pendingActions;
    
    // ===== TIMESTAMPS =====
    private Long generatedTime;
    
    // ===== NESTED CLASSES =====
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardStats {
        // Total counts
        private Integer totalPatients;
        private Integer totalPredictions;
        private Integer todayPredictions;
        
        // Risk distribution
        private Integer highRiskPatients;
        private Integer mediumRiskPatients;
        private Integer lowRiskPatients;
        
        // Health metrics
        private Double averageGlucose;
        private Double averageBMI;
        private Double averageAge;
        
        // Percentages
        private Double criticalAlertPercentage;
        private Double followUpPendingPercentage;
        private Double newPatientsToday;
        
        // Model stats
        private Integer monthlyPredictionCount;
        private Double modelAccuracy;
        
        // Quick counts
        private Integer newlyAddedPatientsToday;
        private Integer criticalAlertsCount;
        private Integer followUpPendingCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartsData {
        private List<TrendData> weeklyTrend;
        private List<TrendData> monthlyTrend;
        private List<TrendData> yearlyTrend;
        
        private List<GlucosePoint> glucoseHistory;
        private List<RiskDistribution> riskDistribution;
        private List<DiabeticDistribution> diabeticDistribution;
        
        private Map<String, Double> featureImportance;
        private List<AgeRiskPoint> ageVsRisk;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        private String date;
        private Integer predictions;
        private Double highRiskPercentage;
        private Double averageConfidence;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GlucosePoint {
        private Long date;
        private Double value;
        private String status;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskDistribution {
        private String riskLevel;
        private Integer count;
        private Double percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiabeticDistribution {
        private String status;
        private Integer count;
        private Double percentage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgeRiskPoint {
        private Integer age;
        private Double riskPercentage;
        private Integer count;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientRiskSummary {
        private String patientId;
        private String patientName;
        private Double currentRisk;
        private String riskLevel;
        private String lastPredictionDate;
        private String trend;  // UP, DOWN, STABLE
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingAction {
        private String actionId;
        private String actionType;
        private String description;
        private String priority;
        private Long dueDate;
        private String assignedTo;
    }
}
