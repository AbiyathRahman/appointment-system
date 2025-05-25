package com.healthcare.appointmentsystem.dto;

import com.healthcare.appointmentsystem.model.AppointmentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentRequestDTO {

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    
    @NotNull(message = "Appointment date and time is required")
    @FutureOrPresent(message = "Appointment date must be in the present or future")
    private LocalDateTime appointmentDateTime;
    
    @NotBlank(message = "Reason for appointment is required")
    @Size(min = 3, max = 255, message = "Reason must be between 3 and 255 characters")
    private String reason;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    private AppointmentStatus status;
}
