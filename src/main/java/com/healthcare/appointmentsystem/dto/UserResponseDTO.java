package com.healthcare.appointmentsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    
    // Additional fields for specific roles
    private DoctorDTO doctorDetails;
    private PatientDTO patientDetails;
}