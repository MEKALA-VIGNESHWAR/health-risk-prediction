package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

/**
 * HeartPredictionResponse DTO - Output data from heart disease prediction
 * Contains prediction results, risk level, confidence, and key factors
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartPredictionResponse {

    private Integer prediction;                    // 0=no disease, 1=disease
    
    @JsonProperty("disease_probability")
    private Double diseaseProbability;            // Probability of heart disease (0-1)
    
    @JsonProperty("no_disease_probability")
    private Double noDiseaseProbability;          // Probability of no disease (0-1)
    
    private String message;                        // Prediction message
    private String risk;                           // Risk level: LOW, MEDIUM, HIGH
    private Double confidenceLevel;                // Confidence (0-1)
    private String modelUsed;                      // Model name/version
    private String predictionId;                   // UUID of prediction record
    
    @JsonProperty("top_factors")
    private List<Map<String, Object>> topFactors; // Top factors affecting prediction
    
    // Recommendations
    private List<String> recommendations;          // Health recommendations
    
    @JsonProperty("risk_description")
    private String riskDescription;               // Detailed risk description

    public HeartPredictionResponse(Integer prediction, Double diseaseProbability, 
                                   Double noDiseaseProbability, String message) {
        this.prediction = prediction;
        this.diseaseProbability = diseaseProbability;
        this.noDiseaseProbability = noDiseaseProbability;
        this.message = message;
    }

}
