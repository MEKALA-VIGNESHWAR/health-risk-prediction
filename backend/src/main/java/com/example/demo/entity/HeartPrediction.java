package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * HeartPrediction Entity - Heart disease risk prediction results
 * Stores comprehensive heart disease prediction results in Supabase PostgreSQL
 * SEPARATE from DiabetesPrediction - maintains modularity
 * Features: age, sex, cp, trestbps, chol, fbs, restecg, thalch, exang, oldpeak, slope, ca, thal
 */
@Entity
@Table(name = "heart_predictions", indexes = {
    @Index(name = "idx_heart_user_id", columnList = "user_id"),
    @Index(name = "idx_heart_result", columnList = "prediction_result"),
    @Index(name = "idx_heart_risk_level", columnList = "risk_level"),
    @Index(name = "idx_heart_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // ===== INPUT FEATURES =====
    @Column(nullable = false)
    private Integer age;              // Age in years

    @Column(nullable = false)
    private Integer sex;              // Sex (0=Female, 1=Male)

    @Column(nullable = false)
    private Integer cp;               // Chest pain type (0-3)

    @Column(name = "trestbps")
    private Double trestbps;          // Resting blood pressure (mm Hg)

    @Column(nullable = false)
    private Double chol;              // Serum cholesterol (mg/dl)

    @Column(nullable = false)
    private Integer fbs;              // Fasting blood sugar (0=no, 1=yes >120mg/dl)

    @Column(name = "restecg")
    private Integer restecg;          // Resting electrocardiographic results (0-2)

    @Column(name = "thalch")
    private Double thalch;            // Maximum heart rate achieved (bpm)

    @Column(nullable = false)
    private Integer exang;            // Exercise induced angina (0=no, 1=yes)

    @Column(name = "oldpeak")
    private Double oldpeak;           // ST depression induced by exercise

    @Column
    private Integer slope;            // Slope of ST segment (0-2) - DEPRECATED

    @Column
    private Integer ca;               // Number of major vessels (0-4) - DEPRECATED

    @Column
    private Integer thal;             // Thalassemia (0-3) - DEPRECATED

    // ===== PREDICTION RESULTS =====
    @Column(name = "prediction_result")
    private Integer predictionResult; // 0=no disease, 1=disease

    @Column(name = "disease_probability")
    private Double diseaseProbability;

    @Column(name = "no_disease_probability")
    private Double noDiseaseProbability;

    @Column(name = "prediction_message", length = 500)
    private String predictionMessage;

    // ===== ENHANCED METRICS =====
    @Column(name = "confidence_level")
    private Double confidenceLevel;

    @Column(name = "risk_level", length = 20)
    private String riskLevel;  // LOW, MEDIUM, HIGH

    @Column(name = "risk_percentage")
    private Double riskPercentage;  // Risk as percentage 0-100

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    // ===== FEATURE IMPORTANCE (JSON stored as string) =====
    @Column(name = "feature_importance", columnDefinition = "TEXT")
    private String featureImportance;

    // ===== RECOMMENDATIONS =====
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;  // JSON array of recommendations

    // ===== METADATA =====
    @Column(name = "prediction_timestamp")
    private Long predictionTimestamp;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        predictionTimestamp = System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
