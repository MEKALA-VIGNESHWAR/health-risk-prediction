package com.example.demo.repository;

import com.example.demo.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * AlertRepository - JPA repository for Alert entity
 * Provides queries for alert management, filtering, and statistics
 */
@Repository
public interface AlertRepositoryJPA extends JpaRepository<Alert, UUID> {

    /** Find all alerts for a patient ordered by newest first */
    List<Alert> findByPatientIdOrderByCreatedAtDesc(UUID patientId);

    /** Find unread alerts for a patient */
    List<Alert> findByPatientIdAndIsReadFalseOrderByCreatedAtDesc(UUID patientId);

    /** Count unread alerts for a patient */
    long countByPatientIdAndIsReadFalse(UUID patientId);

    /** Find alerts by severity */
    List<Alert> findBySeverityOrderByCreatedAtDesc(String severity);

    /** Find alerts by severity for a patient */
    List<Alert> findByPatientIdAndSeverityOrderByCreatedAtDesc(UUID patientId, String severity);

    /** Find all unread alerts ordered by severity priority */
    @Query("SELECT a FROM Alert a WHERE a.isRead = false ORDER BY " +
           "CASE a.severity WHEN 'CRITICAL' THEN 1 WHEN 'HIGH' THEN 2 " +
           "WHEN 'MEDIUM' THEN 3 WHEN 'LOW' THEN 4 ELSE 5 END, a.createdAt DESC")
    List<Alert> findAllUnreadOrderBySeverity();

    /** Find all alerts (paginated) */
    Page<Alert> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /** Find alerts for a patient (paginated) */
    Page<Alert> findByPatientIdOrderByCreatedAtDesc(UUID patientId, Pageable pageable);

    /** Count alerts by severity */
    long countBySeverityAndIsReadFalse(String severity);

    /** Check if alert already exists for a prediction */
    boolean existsByPredictionIdAndAlertType(UUID predictionId, String alertType);

    /** Mark all alerts as read for a patient */
    @Modifying
    @Query("UPDATE Alert a SET a.isRead = true, a.acknowledgedAt = :now WHERE a.patientId = :patientId AND a.isRead = false")
    int markAllReadForPatient(@Param("patientId") UUID patientId, @Param("now") LocalDateTime now);

    /** Count total unread alerts */
    long countByIsReadFalse();

    /** Find recent alerts (last N days) */
    @Query("SELECT a FROM Alert a WHERE a.createdAt >= :since ORDER BY a.createdAt DESC")
    List<Alert> findRecentAlerts(@Param("since") LocalDateTime since);
}
