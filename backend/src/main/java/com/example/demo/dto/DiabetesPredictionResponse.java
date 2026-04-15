package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

/**
 * DiabetesPredictionResponse DTO - Enhanced with advanced ML metrics
 * Includes: predictions, probabilities, confidence levels, risk levels, and feature importance
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
    private String risk;  // LOW, MEDIUM, HIGH, CRITICAL (from new riskLevel field)
    private String riskLevel;  // Preferred over 'risk'

    // ===== MODEL INFO =====
    private String modelUsed;
    private String modelVersion;

    // ===== FEATURE IMPORTANCE =====
    private Map<String, Double> featureImportance;

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
    }
}
