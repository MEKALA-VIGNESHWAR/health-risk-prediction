package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AppointmentTrackingDTO - Track patient appointments and follow-ups
 * Important for continuous patient monitoring and care management
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentTrackingDTO {
    private String appointmentId;
    private String patientId;
    private String patientName;
    private String doctorId;
    private String doctorName;
    
    // Appointment Details
    private Long appointmentDate;
    private String appointmentTime;
    private String appointmentType;  // FOLLOW_UP, CHECKUP, CONSULTATION, EMERGENCY
    private String status;  // SCHEDULED, COMPLETED, CANCELLED, NO_SHOW
    
    // Visit Information
    private String visitReason;
    private String visitNotes;
    private Long lastVisitDate;
    private Integer daysSinceLastVisit;
    
    // Follow-up Status
    private Boolean followUpDue;
    private Long nextFollowUpDate;
    private String followUpDescription;
    private String followUpStatus;  // PENDING, COMPLETED, OVERDUE
    
    // Reminders
    private Boolean reminderSent;
    private Long reminderSentDate;
    private String reminderMethod;  // EMAIL, SMS, IN_APP
    
    // Test/Lab Results Due
    private Boolean testsDue;
    private String testsDueList;  // comma-separated test names
    private Long testsDueDate;
    
    // Timestamps
    private Long createdDate;
    private Long updatedDate;
    
    // Additional Info
    private String location;
    private String consultationMode;  // IN_PERSON, VIRTUAL, HYBRID
    private String conferenceLink;  // for virtual consultations
}
