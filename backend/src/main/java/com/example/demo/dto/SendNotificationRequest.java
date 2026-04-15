package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SendNotificationRequest - request to send notification from doctor to patient
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {
    private String doctorId;
    private String patientId;
    private String title;
    private String message;
    private String type; // WARNING, INFO, CRITICAL, RECOMMENDATION
}
