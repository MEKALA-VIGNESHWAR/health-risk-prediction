package com.example.demo.repository;

import com.example.demo.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * NotificationRepository - JPA repository for Notification entity
 */
@Repository
public interface NotificationRepositoryJPA extends JpaRepository<Notification, UUID> {
    List<Notification> findByPatientId(UUID patientId);
    List<Notification> findByDoctorId(UUID doctorId);
    List<Notification> findByPatientIdAndIsReadFalse(UUID patientId);
    long countByPatientIdAndIsReadFalse(UUID patientId);
}
