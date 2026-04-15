package com.example.demo.controller;

import com.example.demo.dto.NotificationDTO;
import com.example.demo.dto.SendNotificationRequest;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationRepositoryJPA;
import com.example.demo.repository.UserRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * NotificationController - REST endpoints for notifications
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    private final NotificationRepositoryJPA notificationRepository;
    private final UserRepositoryJPA userRepository;

    /**
     * Get all notifications for a patient
     * GET /api/notifications/patient/{patientId}
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientNotifications(@PathVariable String patientId) {
        try {
            UUID patientUUID = UUID.fromString(patientId);
            List<Notification> notifications = notificationRepository.findByPatientId(patientUUID);
            
            List<NotificationDTO> dtos = notifications.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            log.info("Retrieved {} notifications for patient {}", dtos.size(), patientId);
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid patient ID", 400));
        } catch (Exception e) {
            log.error("Error fetching patient notifications: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to fetch notifications", 500));
        }
    }

    /**
     * Get unread notifications count for a patient
     * GET /api/notifications/patient/{patientId}/unread-count
     */
    @GetMapping("/patient/{patientId}/unread-count")
    public ResponseEntity<?> getUnreadCount(@PathVariable String patientId) {
        try {
            UUID patientUUID = UUID.fromString(patientId);
            long unreadCount = notificationRepository.countByPatientIdAndIsReadFalse(patientUUID);
            return ResponseEntity.ok(new UnreadCountResponse(unreadCount));
        } catch (Exception e) {
            log.error("Error fetching unread count: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to fetch unread count", 500));
        }
    }

    /**
     * Send notification from doctor to patient
     * POST /api/notifications/send
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody SendNotificationRequest request) {
        try {
            log.info("Sending notification to patient: {}", request.getPatientId());

            if (request.getPatientId() == null || request.getPatientId().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Patient ID is required", 400));
            }

            if (request.getTitle() == null || request.getTitle().isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Title is required", 400));
            }

            UUID patientUUID = UUID.fromString(request.getPatientId());
            User patient = userRepository.findById(patientUUID).orElse(null);

            if (patient == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Patient not found", 400));
            }

            Notification notification = new Notification();
            notification.setPatientId(patientUUID);
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setType(request.getType() != null ? request.getType() : "INFO");
            notification.setIsRead(false);

            // Note: Doctor ID will be injected from the authenticated user in the service layer
            // For now, we'll need to pass it or get it from the request
            
            Notification saved = notificationRepository.save(notification);
            log.info("Notification sent successfully: {}", saved.getId());

            return ResponseEntity.ok(new SuccessMessageResponse("Notification sent successfully", saved.getId().toString()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid patient ID format", 400));
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to send notification", 500));
        }
    }

    /**
     * Mark notification as read
     * PUT /api/notifications/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<?> markAsRead(@PathVariable String notificationId) {
        try {
            UUID notificationUUID = UUID.fromString(notificationId);
            Notification notification = notificationRepository.findById(notificationUUID).orElse(null);

            if (notification == null) {
                return ResponseEntity.notFound().build();
            }

            notification.setIsRead(true);
            notificationRepository.save(notification);

            log.info("Notification marked as read: {}", notificationId);
            return ResponseEntity.ok(new SuccessMessageResponse("Notification marked as read", ""));
        } catch (Exception e) {
            log.error("Error marking notification as read: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to mark as read", 500));
        }
    }

    /**
     * Delete notification
     * DELETE /api/notifications/{notificationId}
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable String notificationId) {
        try {
            UUID notificationUUID = UUID.fromString(notificationId);
            notificationRepository.deleteById(notificationUUID);
            log.info("Notification deleted: {}", notificationId);
            return ResponseEntity.ok(new SuccessMessageResponse("Notification deleted", ""));
        } catch (Exception e) {
            log.error("Error deleting notification: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to delete notification", 500));
        }
    }

    /**
     * Convert Notification entity to DTO with doctor and patient names
     */
    private NotificationDTO convertToDTO(Notification notification) {
        User doctor = notification.getDoctorId() != null ? userRepository.findById(notification.getDoctorId()).orElse(null) : null;
        User patient = notification.getPatientId() != null ? userRepository.findById(notification.getPatientId()).orElse(null) : null;

        return new NotificationDTO(
                notification.getId().toString(),
                notification.getDoctorId() != null ? notification.getDoctorId().toString() : null,
                doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : "Unknown Doctor",
                notification.getPatientId().toString(),
                patient != null ? patient.getFirstName() + " " + patient.getLastName() : "Unknown Patient",
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getIsRead(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class ErrorResponse {
        private String error;
        private int status;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class SuccessMessageResponse {
        private String message;
        private String data;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class UnreadCountResponse {
        private long unreadCount;
    }
}
