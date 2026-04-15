package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * MLInsightsDTO - ML Model explainability and insights
 * Shows why a prediction happened and which features contributed most
 * VERY IMPORTANT for project demo and industry credibility
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MLInsightsDTO {
    private String predictionId;
    private String patientId;
    
    // ===== PREDICTION DETAILS =====
    private Integer prediction;  // 0 or 1
    private String predictionStatus;  // DIABETIC, NON_DIABETIC, HIGH_RISK, LOW_RISK
    
    // ===== CONFIDENCE & PROBABILITY =====
    private Double confidenceLevel;  // 0-100%
    private String confidenceText;  // HIGH, MEDIUM, LOW
    private Double probabilityDiabetes;
    private Double probabilityNoDiabetes;
    
    // ===== FEATURE IMPORTANCE =====
    private Map<String, Double> featureImportance;  // e.g., {"Glucose": 45, "BMI": 20, ...}
    private List<FeatureContribution> topContributingFeatures;  // Top 3-5 features
    
    // ===== FEATURE VALUES AT PREDICTION TIME =====
    private Map<String, Double> inputFeatures;  // actual input values
    
    // ===== MODEL EXPLANATION =====
    private String explanation;  // Natural language explanation
    private String riskFactors;  // "High glucose, High BMI, Age > 45"
    private String protectiveFactors;  // "Good insulin levels, Normal BP"
    
    // ===== SHAP VALUES (advanced explainability) =====
    private Map<String, Double> shapValues;  // SHAP values for each feature
    private Double baselineValue;  // Model base prediction
    private Double predictionValue;  // Final prediction value
    
    // ===== THRESHOLDS & RANGES =====
    private String glucoseStatus;  // NORMAL, HIGH, CRITICAL
    private String bmiStatus;      // UNDERWEIGHT, NORMAL, OVERWEIGHT, OBESE
    private String insulinStatus;  // NORMAL, LOW, HIGH
    private String ageStatus;      // YOUNG, MIDDLE_AGED, SENIOR
    
    // ===== MODEL METADATA =====
    private String modelName;
    private String modelVersion;
    private String modelType;  // RANDOM_FOREST, GRADIENT_BOOSTING, NEURAL_NETWORK, etc.
    private String trainingDataset;
    private Long modelTrainedDate;
    
    // ===== PERFORMANCE METRICS =====
    private Double modelAccuracy;
    private Double modelPrecision;
    private Double modelRecall;
    private Double modelF1Score;
    private Double modelROC_AUC;
    
    // ===== TIMESTAMP =====
    private Long timestamp;
    private Long predictionDate;
    
    // ===== NESTED CLASS =====
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureContribution {
        private String featureName;
        private Double contributionPercentage;
        private Double featureValue;
        private String impact;  // POSITIVE, NEGATIVE, NEUTRAL
        private String interpretation;  // Human-readable interpretation
    }
}
