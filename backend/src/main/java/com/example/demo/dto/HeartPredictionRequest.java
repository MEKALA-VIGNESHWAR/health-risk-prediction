package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HeartPredictionRequest DTO - Input data for heart disease prediction
 * Features (13): age, sex, cp, trestbps, chol, fbs, restecg, thalach, exang, oldpeak, slope, ca, thal
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartPredictionRequest {

    private Integer age;              // Age in years
    private Integer sex;              // Sex (0=Female, 1=Male)
    private Integer cp;               // Chest pain type (0-3)
    private Double trestbps;          // Resting blood pressure (mm Hg)
    private Double chol;              // Serum cholesterol (mg/dl)
    private Integer fbs;              // Fasting blood sugar (0=no, 1=yes >120mg/dl)
    private Integer restecg;          // Resting electrocardiographic results (0-2)
    private Double thalach;           // Maximum heart rate achieved (bpm) - CORRECTED from thalch
    private Integer exang;            // Exercise induced angina (0=no, 1=yes)
    private Double oldpeak;           // ST depression induced by exercise
    private Integer slope;            // Slope of the ST segment (0-2)
    private Integer ca;               // Number of major vessels (0-3)
    private Integer thal;             // Thalassemia type (0-2)
    private String userId;            // Optional: user ID if logged in

}
