package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DoctorNote Entity - Stores timestamped doctor notes for patients
 * Maintains full history of all notes added by doctors
 */
@Entity
@Table(name = "doctor_notes", indexes = {
    @Index(name = "idx_notes_patient_id", columnList = "patient_id"),
    @Index(name = "idx_notes_doctor_id", columnList = "doctor_id"),
    @Index(name = "idx_notes_prediction_id", columnList = "prediction_id"),
    @Index(name = "idx_notes_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorNote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;

    @Column(name = "prediction_id")
    private UUID predictionId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** GENERAL, FOLLOW_UP, PRESCRIPTION, REVIEW, CRITICAL */
    @Column(name = "note_type", length = 50)
    private String noteType = "GENERAL";

    @Column(name = "doctor_name", length = 255)
    private String doctorName;

    @Column(name = "patient_name", length = 255)
    private String patientName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (noteType == null) {
            noteType = "GENERAL";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
