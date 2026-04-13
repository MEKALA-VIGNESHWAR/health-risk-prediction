package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DiabetesPredictionRequest DTO - Input data for prediction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiabetesPredictionRequest {

    private Integer pregnancies;
    private Integer glucose;
    private Integer bloodPressure;
    private Integer skinThickness;
    private Integer insulin;
    private Double bmi;
    private Double diabetesPedigreeFunction;
    private Integer age;
    private String userId; // Optional: user ID if logged in (UUID string)

}
