package com.example.demo.repository;

import com.example.demo.entity.DoctorNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * DoctorNoteRepository - JPA repository for DoctorNote entity
 */
@Repository
public interface DoctorNoteRepositoryJPA extends JpaRepository<DoctorNote, UUID> {

    /** Find all notes for a patient, newest first */
    List<DoctorNote> findByPatientIdOrderByCreatedAtDesc(UUID patientId);

    /** Find notes by doctor for a patient */
    List<DoctorNote> findByPatientIdAndDoctorIdOrderByCreatedAtDesc(UUID patientId, UUID doctorId);

    /** Find notes for a specific prediction */
    List<DoctorNote> findByPredictionIdOrderByCreatedAtDesc(UUID predictionId);

    /** Count notes for a patient */
    long countByPatientId(UUID patientId);
}
