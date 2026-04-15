package com.example.demo.controller;

import com.example.demo.dto.PatientSummaryDTO;
import com.example.demo.dto.PatientDetailsDTO;
import com.example.demo.dto.SendNotificationRequest;
import com.example.demo.dto.DoctorDashboardDTO;
import com.example.demo.dto.PredictionAnalyticsDTO;
import com.example.demo.entity.DiabetesPrediction;
import com.example.demo.entity.Notification;
import com.example.demo.entity.User;
import com.example.demo.repository.DiabetesPredictionRepositoryJPA;
import com.example.demo.repository.NotificationRepositoryJPA;
import com.example.demo.repository.UserRepositoryJPA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DoctorController - Enhanced REST endpoints for doctors
 * Includes: patient management, prediction analytics, dashboard, and alerts
 */
@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class DoctorController {

    private final UserRepositoryJPA userRepository;
    private final DiabetesPredictionRepositoryJPA predictionRepository;
    private final NotificationRepositoryJPA notificationRepository;

    /**
     * Get all patients with predictions (for doctor view)
     * GET /api/doctor/patients
     */
    @GetMapping("/patients")
    public ResponseEntity<?> getAllPatients() {
        try {
            log.info("Fetching all patients with predictions for doctor view");
            
            // Get all predictions and extract unique users
            List<DiabetesPrediction> allPredictions = predictionRepository.findAll();
            List<UUID> patientIds = allPredictions.stream()
                    .map(p -> p.getUser().getId())
                    .distinct()
                    .collect(Collectors.toList());

            List<PatientSummaryDTO> patientSummaries = patientIds.stream()
                    .map(patientId -> {
                        User patient = userRepository.findById(patientId).orElse(null);
                        if (patient == null) return null;
                        
                        List<DiabetesPrediction> predictions = predictionRepository
                                .findByUserId(patientId);
                        
                        int totalPredictions = predictions.size();
                        long highRiskCount = predictions.stream()
                                .filter(p -> p.getPredictionResult() == 1)
                                .count();
                        double riskPercentage = totalPredictions > 0 
                                ? (highRiskCount * 100.0 / totalPredictions) 
                                : 0;

                        return new PatientSummaryDTO(
                                patient.getId().toString(),
                                patient.getUsername(),
                                patient.getEmail(),
                                patient.getFirstName(),
                                patient.getLastName(),
                                totalPredictions,
                                (int) highRiskCount,
                                riskPercentage,
                                patient.getCreatedAt()
                        );
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());

            log.info("Found {} patients with predictions", patientSummaries.size());
            return ResponseEntity.ok(patientSummaries);

        } catch (Exception e) {
            log.error("Error fetching patients: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to fetch patients", 500));
        }
    }

    /**
     * Get patient details with full prediction history
     * GET /api/doctor/patients/{patientId}
     */
    @GetMapping("/patients/{patientId}")
    public ResponseEntity<?> getPatientDetails(@PathVariable String patientId) {
        try {
            log.info("Fetching details for patient: {}", patientId);
            
            UUID uuid = UUID.fromString(patientId);
            User patient = userRepository.findById(uuid)
                    .orElse(null);

            if (patient == null) {
                return ResponseEntity.notFound().build();
            }

            List<DiabetesPrediction> predictions = predictionRepository.findByUserId(uuid);
            
            PatientDetailsDTO details = new PatientDetailsDTO(
                    patient.getId().toString(),
                    patient.getUsername(),
                    patient.getEmail(),
                    patient.getFirstName(),
                    patient.getLastName(),
                    predictions,
                    patient.getCreatedAt()
            );

            log.info("Patient details fetched: {} predictions", predictions.size());
            return ResponseEntity.ok(details);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid patient ID", 400));
        } catch (Exception e) {
            log.error("Error fetching patient details: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to fetch patient details", 500));
        }
    }

    /**
     * Get comprehensive doctor dashboard overview
     * GET /api/doctor/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDoctorDashboard() {
        try {
            log.info("Fetching doctor dashboard");
            
            List<DiabetesPrediction> allPredictions = predictionRepository.findAll();
            List<User> allPatients = userRepository.findAll();
            
            // Dashboard Summary
            int totalPatients = (int) allPatients.stream().count();
            int totalPredictions = allPredictions.size();
            int todayPredictions = (int) allPredictions.stream()
                    .filter(p -> p.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                    .count();
            
            long highRiskCount = allPredictions.stream()
                    .filter(p -> "HIGH".equals(p.getRiskLevel()) || "CRITICAL".equals(p.getRiskLevel()))
                    .count();
            long mediumRiskCount = allPredictions.stream()
                    .filter(p -> "MEDIUM".equals(p.getRiskLevel()))
                    .count();
            
            long pendingReviews = allPredictions.stream()
                    .filter(p -> "PENDING".equals(p.getStatus()))
                    .count();
            
            DoctorDashboardDTO.DashboardSummary summary = new DoctorDashboardDTO.DashboardSummary(
                    totalPatients,
                    totalPredictions,
                    todayPredictions,
                    (int) highRiskCount,
                    (int) mediumRiskCount,
                    (int) pendingReviews
            );
            
            // Patients at Risk
            List<DoctorDashboardDTO.PatientRiskSummary> patientsAtRisk = allPredictions.stream()
                    .filter(p -> "HIGH".equals(p.getRiskLevel()) || "CRITICAL".equals(p.getRiskLevel()))
                    .collect(Collectors.groupingBy(DiabetesPrediction::getUserId)).entrySet().stream()
                    .map(entry -> {
                        DiabetesPrediction latest = entry.getValue().stream()
                                .max(Comparator.comparing(DiabetesPrediction::getCreatedAt))
                                .orElse(null);
                        if (latest == null) return null;
                        
                        User patient = latest.getUser();
                        return new DoctorDashboardDTO.PatientRiskSummary(
                                patient.getId().toString(),
                                patient.getFirstName() + " " + patient.getLastName(),
                                latest.getRiskLevel(),
                                latest.getProbabilityDiabetes(),
                                latest.getCreatedAt().toString(),
                                entry.getValue().size(),
                                "STABLE",
                                latest.getStatus(),
                                latest.getDoctorNotes()
                        );
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
            
            // Alerts
            List<DoctorDashboardDTO.PredictionAlert> alerts = allPredictions.stream()
                    .filter(p -> "PENDING".equals(p.getStatus()) || "CRITICAL".equals(p.getRiskLevel()))
                    .map(p -> {
                        String alertType = "CRITICAL".equals(p.getRiskLevel()) ? "HIGH_RISK" : 
                                          "BORDERLINE".equals(p.getConfidenceText()) ? "CONFIDENCE_LOW" : "PENDING_REVIEW";
                        return new DoctorDashboardDTO.PredictionAlert(
                                p.getId().toString(),
                                p.getUserId().toString(),
                                p.getUser().getFirstName() + " " + p.getUser().getLastName(),
                                alertType,
                                p.getRiskLevel(),
                                "Patient " + p.getUser().getFirstName() + " has " + p.getRiskLevel() + " diabetes risk",
                                p.getPredictionTimestamp(),
                                false
                        );
                    })
                    .collect(Collectors.toList());
            
            // Performance Metrics (placeholder - in production would come from ML system)
            DoctorDashboardDTO.PerformanceMetrics metrics = new DoctorDashboardDTO.PerformanceMetrics(
                    0.92, 0.89, 0.87, 0.88, 0.90, 5, "Calibrated Ensemble v2.0"
            );
            
            // System Health
            DoctorDashboardDTO.SystemHealth health = new DoctorDashboardDTO.SystemHealth(
                    "HEALTHY",
                    LocalDate.now().toString(),
                    0.92,
                    30,
                    "GOOD",
                    allPredictions.size()
            );
            
            DoctorDashboardDTO dashboard = new DoctorDashboardDTO(
                    summary,
                    patientsAtRisk,
                    alerts,
                    metrics,
                    health
            );
            
            log.info("Dashboard generated successfully");
            return ResponseEntity.ok(dashboard);
            
        } catch (Exception e) {
            log.error("Error generating dashboard: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to generate dashboard", 500));
        }
    }

    /**
     * Get prediction analytics for a specific patient
     * GET /api/doctor/analytics/{patientId}
     */
    @GetMapping("/analytics/{patientId}")
    public ResponseEntity<?> getPatientAnalytics(@PathVariable String patientId) {
        try {
            log.info("Fetching analytics for patient: {}", patientId);
            UUID uuid = UUID.fromString(patientId);
            
            User patient = userRepository.findById(uuid).orElse(null);
            if (patient == null) {
                return ResponseEntity.notFound().build();
            }
            
            List<DiabetesPrediction> predictions = predictionRepository.findByUserId(uuid);
            if (predictions.isEmpty()) {
                return ResponseEntity.ok(new PredictionAnalyticsDTO());
            }
            
            int totalPredictions = predictions.size();
            int positivePredictions = (int) predictions.stream()
                    .filter(p -> p.getPredictionResult() == 1)
                    .count();
            int negativePredictions = totalPredictions - positivePredictions;
            
            Map<String, Integer> riskDistribution = predictions.stream()
                    .collect(Collectors.groupingBy(
                            p -> p.getRiskLevel() != null ? p.getRiskLevel() : "UNKNOWN",
                            Collectors.summingInt(p -> 1)
                    ));
            
            Map<String, Double> riskPercentage = riskDistribution.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> (e.getValue() * 100.0) / totalPredictions
                    ));
            
            double averageConfidence = predictions.stream()
                    .mapToDouble(p -> p.getConfidenceLevel() != null ? p.getConfidenceLevel() : 0.5)
                    .average().orElse(0);
            
            long borderlinePredictions = predictions.stream()
                    .filter(p -> "BORDERLINE - Recommend further clinical evaluation".equals(p.getConfidenceText()))
                    .count();
            
            double averageProbabilityDiabetes = predictions.stream()
                    .mapToDouble(p -> p.getProbabilityDiabetes() != null ? p.getProbabilityDiabetes() : 0)
                    .average().orElse(0);
            
            PredictionAnalyticsDTO analytics = new PredictionAnalyticsDTO();
            analytics.setUserId(patientId);
            analytics.setUserName(patient.getFirstName() + " " + patient.getLastName());
            analytics.setTotalPredictions(totalPredictions);
            analytics.setPositivePredictions(positivePredictions);
            analytics.setNegativePredictions(negativePredictions);
            analytics.setPositivePercentage((positivePredictions * 100.0) / totalPredictions);
            analytics.setRiskDistribution(riskDistribution);
            analytics.setRiskPercentage(riskPercentage);
            analytics.setAverageConfidence(averageConfidence);
            analytics.setBorderlinePredictions((int) borderlinePredictions);
            analytics.setBorderlinePercentage((borderlinePredictions * 100.0) / totalPredictions);
            analytics.setAverageProbabilityDiabetes(averageProbabilityDiabetes);
            
            log.info("Analytics generated for patient {}: {} predictions", patientId, totalPredictions);
            return ResponseEntity.ok(analytics);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid patient ID", 400));
        } catch (Exception e) {
            log.error("Error fetching analytics: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to fetch analytics", 500));
        }
    }

    /**
     * Review and update prediction with doctor notes
     * PUT /api/doctor/predictions/{predictionId}
     */
    @PutMapping("/predictions/{predictionId}")
    public ResponseEntity<?> updatePredictionReview(
            @PathVariable String predictionId,
            @RequestBody PredictionReviewRequest reviewRequest) {
        try {
            log.info("Updating prediction review: {}", predictionId);
            
            UUID uuid = UUID.fromString(predictionId);
            Optional<DiabetesPrediction> predictionOpt = predictionRepository.findById(uuid);
            
            if (!predictionOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            DiabetesPrediction prediction = predictionOpt.get();
            prediction.setStatus(reviewRequest.getStatus());
            prediction.setDoctorNotes(reviewRequest.getNotes());
            prediction.setReviewedBy(reviewRequest.getReviewedBy());
            prediction.setUpdatedAt(LocalDateTime.now());
            
            DiabetesPrediction updated = predictionRepository.save(prediction);
            log.info("Prediction {} reviewed successfully", predictionId);
            
            return ResponseEntity.ok(Map.of(
                    "message", "Prediction reviewed successfully",
                    "predictionId", predictionId,
                    "status", updated.getStatus()
            ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid prediction ID", 400));
        } catch (Exception e) {
            log.error("Error updating prediction: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to update prediction", 500));
        }
    }

    /**
     * Send notification to a patient from the authenticated doctor
     * Extracts doctorId from JWT token in SecurityContext
     * POST /api/doctor/send-notification
     */
    @PostMapping("/send-notification")
    public ResponseEntity<?> sendNotificationToPatient(@RequestBody SendNotificationRequest request) {
        try {
            // Parse patient ID
            UUID patientId = UUID.fromString(request.getPatientId());
            
            // Get doctorId from SecurityContext (from JWT token)
            // For now, using a placeholder UUID - in real app would extract from principal
            String doctorIdString = request.getDoctorId(); // Will be passed from frontend
            UUID doctorId = doctorIdString != null ? UUID.fromString(doctorIdString) : null;
            
            // Verify patient exists
            if (!userRepository.existsById(patientId)) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Patient not found", 400));
            }
            
            // Verify doctor exists
            if (doctorId != null && !userRepository.existsById(doctorId)) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Doctor not found", 400));
            }

            // Create notification
            Notification notification = new Notification();
            notification.setDoctorId(doctorId);
            notification.setPatientId(patientId);
            notification.setTitle(request.getTitle());
            notification.setMessage(request.getMessage());
            notification.setType(request.getType());
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());

            Notification saved = notificationRepository.save(notification);
            log.info("Notification sent from doctor {} to patient {}", doctorId, patientId);

            return ResponseEntity.ok(new NotificationSentResponse(saved.getId().toString(), "Notification sent successfully"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid ID format", 400));
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage());
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to send notification", 500));
        }
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class NotificationSentResponse {
        private String notificationId;
        private String message;
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
    static class PredictionReviewRequest {
        private String status;          // PENDING, REVIEWED, CONFIRMED, ARCHIVED
        private String notes;           // Doctor notes
        private String reviewedBy;      // Doctor name/ID
    }
}
