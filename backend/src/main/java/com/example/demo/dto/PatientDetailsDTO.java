package com.example.demo.dto;

import com.example.demo.entity.DiabetesPrediction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * PatientDetailsDTO - detailed patient info with full prediction history for doctor's patient detail view
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDetailsDTO {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<DiabetesPrediction> predictions;
    private LocalDateTime registeredDate;
}
