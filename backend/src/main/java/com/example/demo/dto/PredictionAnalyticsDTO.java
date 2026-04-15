package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * Analytics DTO for prediction history and trends
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionAnalyticsDTO {

    // ===== USER STATISTICS =====
    private String userId;
    private String userName;
    private Integer totalPredictions;
    private Integer positivePredictions;
    private Integer negativePredictions;
    private Double positivePercentage;

    // ===== RISK DISTRIBUTION =====
    private Map<String, Integer> riskDistribution;  // LOW, MEDIUM, HIGH, CRITICAL
    private Map<String, Double> riskPercentage;

    // ===== TEMPORAL TRENDS =====
    private List<DailyPredictionCount> dailyTrends;
    private List<WeeklyPredictionCount> weeklyTrends;
    private List<MonthlyPredictionCount> monthlyTrends;

    // ===== PREDICTION QUALITY =====
    private Double averageConfidence;
    private Double confidenceStdDev;
    private Integer borderlinePredictions;
    private Double borderlinePercentage;

    // ===== METRICS =====
    private Double averageProbabilityDiabetes;
    private Double minProbabilityDiabetes;
    private Double maxProbabilityDiabetes;

    // ===== HEALTH INDICATORS =====
    private Map<String, Double> averageHealthMetrics;  // avg glucose, BMI, etc. for positive cases
    private List<RiskTrendPoint> riskTrendHistory;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyPredictionCount {
        private String date;
        private Integer count;
        private Integer positive;
        private Integer negative;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyPredictionCount {
        private String week;
        private Integer count;
        private Double riskScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyPredictionCount {
        private String month;
        private Integer count;
        private Double riskScore;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskTrendPoint {
        private String date;
        private String riskLevel;
        private Double probability;
    }
}
