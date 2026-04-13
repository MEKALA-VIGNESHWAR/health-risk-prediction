package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DiabetesPredictionResponse DTO - Output prediction results
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiabetesPredictionResponse {

    private String predictionId;
    private Integer prediction;
    private Double probabilityNoDiabetes;
    private Double probabilityDiabetes;
    private String message;
    private String risk;
    private Double confidenceLevel;
    private String modelUsed;

    public DiabetesPredictionResponse(Integer prediction, Double probNoDiab, Double probDiab, String message) {
        this.prediction = prediction;
        this.probabilityNoDiabetes = probNoDiab;
        this.probabilityDiabetes = probDiab;
        this.message = message;
        this.risk = prediction == 1 ? "HIGH" : "LOW";
    }

}
