package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DiabetesPrediction Entity - stores diabetes prediction results in Supabase PostgreSQL
 */
@Entity
@Table(name = "diabetes_predictions", indexes = {
    @Index(name = "idx_predictions_user_id", columnList = "user_id"),
    @Index(name = "idx_predictions_result", columnList = "prediction_result")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiabetesPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(nullable = false)
    private Integer pregnancies;

    @Column(nullable = false)
    private Integer glucose;

    @Column(name = "blood_pressure")
    private Integer bloodPressure;

    @Column(name = "skin_thickness")
    private Integer skinThickness;

    @Column(nullable = false)
    private Integer insulin;

    @Column(nullable = false)
    private java.math.BigDecimal bmi;

    @Column(name = "diabetes_pedigree_function")
    private java.math.BigDecimal diabetesPedigreeFunction;

    @Column(nullable = false)
    private Integer age;

    @Column(name = "prediction_result")
    private Integer predictionResult;

    @Column(name = "probability_no_diabetes")
    private Double probabilityNoDiabetes;

    @Column(name = "probability_diabetes")
    private Double probabilityDiabetes;

    @Column(name = "prediction_message", length = 500)
    private String predictionMessage;

    @Column(name = "created_date", nullable = false, updatable = false)
    private Long createdDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (createdDate == null) {
            createdDate = System.currentTimeMillis();
        }
    }
}

