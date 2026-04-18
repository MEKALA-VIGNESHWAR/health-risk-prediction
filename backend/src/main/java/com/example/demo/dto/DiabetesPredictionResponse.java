package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * DiabetesPredictionResponse DTO - Industry-level prediction response
 * Includes: predictions, probabilities, confidence, risk, feature importance,
 * abnormal values, recommendations, and previous comparison
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiabetesPredictionResponse {

    // ===== BASIC PREDICTION =====
    private String predictionId;
    private Integer prediction;
    private String message;

    // ===== PROBABILITIES =====
    private Double probabilityNoDiabetes;
    private Double probabilityDiabetes;

    // ===== CONFIDENCE & RISK =====
    private Double confidenceLevel;
    private String confidenceText;
    private String risk;       // LOW, MEDIUM, HIGH, CRITICAL
    private String riskLevel;  // Preferred over 'risk'
    private Double riskPercentage;  // 0-100

    // ===== MODEL INFO =====
    private String modelUsed;
    private String modelVersion;

    // ===== FEATURE IMPORTANCE =====
    private Map<String, Double> featureImportance;

    // ===== ABNORMAL VALUES =====
    private Map<String, Object> abnormalValues;

    // ===== SMART RECOMMENDATIONS =====
    private List<Map<String, String>> recommendations;

    // ===== PREVIOUS COMPARISON =====
    private Map<String, Object> previousComparison;

    // ===== METADATA =====
    private Long timestamp;
    private String status;

    // ===== DOCTOR REVIEW =====
    private String doctorNotes;
    private String reviewedBy;

    // Legacy constructor for backward compatibility
    public DiabetesPredictionResponse(Integer prediction, Double probNoDiab, Double probDiab, String message) {
        this.prediction = prediction;
        this.probabilityNoDiabetes = probNoDiab;
        this.probabilityDiabetes = probDiab;
        this.message = message;
        this.risk = prediction == 1 ? "HIGH" : "LOW";
        this.riskLevel = prediction == 1 ? "HIGH" : "LOW";
        this.riskPercentage = probDiab != null ? probDiab * 100.0 : 0.0;
    }
}
