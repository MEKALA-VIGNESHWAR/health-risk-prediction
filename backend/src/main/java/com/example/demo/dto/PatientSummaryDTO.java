package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * PatientSummaryDTO - summary info for patients in doctor's patient list
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientSummaryDTO {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private int totalPredictions;
    private int highRiskCount;
    private double riskPercentage;
    private LocalDateTime registeredDate;
}
