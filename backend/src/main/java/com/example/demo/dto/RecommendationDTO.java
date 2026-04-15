package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * RecommendationDTO - Health recommendations based on prediction and risk factors
 * VERY IMPORTANT feature - doctors love actionable recommendations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private String recommendationId;
    private String patientId;
    private String predictionId;
    
    // ===== RECOMMENDATION CATEGORY =====
    private String category;  // DIETARY, EXERCISE, MEDICAL, LIFESTYLE, MONITORING
    
    // ===== RECOMMENDATIONS LIST =====
    private List<Recommendation> recommendations;
    
    // ===== PRIORITY =====
    private String priority;  // HIGH, MEDIUM, LOW
    private Integer priorityScore;  // 1-10
    
    // ===== PERSONALIZATION =====
    private String basedOnFactors;  // e.g., "High glucose, High BMI, Age"
    private String riskLevel;
    private Integer riskPercentage;
    
    // ===== URGENCY =====
    private Boolean isUrgent;
    private String urgencyMessage;
    
    // ===== TIMELINE =====
    private String implementationTimeline;  // IMMEDIATELY, THIS_WEEK, THIS_MONTH
    private Long reviewDate;
    
    // ===== SUCCESS METRICS =====
    private List<SuccessMetric> successMetrics;
    
    // ===== DOCTOR NOTES =====
    private String doctorNotes;
    private String approvedBy;
    private Long approvalDate;
    
    // ===== TIMESTAMP =====
    private Long createdDate;
    private Long updatedDate;
    
    // ===== NESTED CLASSES =====
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recommendation {
        private String recommendationText;
        private String category;  // Diet, Exercise, Monitoring, Medical
        private String impact;  // Impact description
        private Integer priority;  // 1-10
        private String frequency;  // Daily, Weekly, Monthly
        private String duration;  // 1-4 weeks, ongoing
        private String difficulty;  // EASY, MEDIUM, HARD
        private String expectedOutcome;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuccessMetric {
        private String metricName;  // e.g., "Reduce glucose to <140"
        private Double targetValue;
        private String unit;
        private String timeframe;
        private Double currentValue;
        private Boolean achieved;
    }
}
