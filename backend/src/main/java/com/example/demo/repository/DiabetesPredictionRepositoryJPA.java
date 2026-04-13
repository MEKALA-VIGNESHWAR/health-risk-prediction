package com.example.demo.repository;

import com.example.demo.entity.DiabetesPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * DiabetesPrediction Repository - JPA interface for DiabetesPrediction entity (PostgreSQL)
 */
@Repository
public interface DiabetesPredictionRepositoryJPA extends JpaRepository<DiabetesPrediction, UUID> {

    /**
     * Find all predictions by user ID
     */
    List<DiabetesPrediction> findByUserId(UUID userId);

    /**
     * Find all predictions by prediction result
     */
    List<DiabetesPrediction> findByPredictionResult(Integer predictionResult);

    /**
     * Count predictions by user ID
     */
    long countByUserId(UUID userId);

    /**
     * Delete all predictions by user ID (cascade delete handled by FK)
     */
    void deleteByUserId(UUID userId);
}
