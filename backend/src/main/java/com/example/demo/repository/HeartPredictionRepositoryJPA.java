package com.example.demo.repository;

import com.example.demo.entity.HeartPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * HeartPrediction Repository - JPA interface for HeartPrediction entity (PostgreSQL)
 * Separate from DiabetesPredictionRepositoryJPA - maintains modularity
 */
@Repository
public interface HeartPredictionRepositoryJPA extends JpaRepository<HeartPrediction, UUID> {

    /**
     * Find all heart predictions by user ID
     */
    List<HeartPrediction> findByUserId(UUID userId);

    /**
     * Find all heart predictions by prediction result (0 or 1)
     */
    List<HeartPrediction> findByPredictionResult(Integer predictionResult);

    /**
     * Find all heart predictions by risk level
     */
    List<HeartPrediction> findByRiskLevel(String riskLevel);

    /**
     * Find all heart predictions by user ID and risk level
     */
    List<HeartPrediction> findByUserIdAndRiskLevel(UUID userId, String riskLevel);

    /**
     * Count predictions by user ID
     */
    long countByUserId(UUID userId);

    /**
     * Count high-risk predictions (by risk level)
     */
    long countByUserIdAndRiskLevel(UUID userId, String riskLevel);

    /**
     * Delete all predictions by user ID (cascade delete handled by FK)
     */
    void deleteByUserId(UUID userId);
}
