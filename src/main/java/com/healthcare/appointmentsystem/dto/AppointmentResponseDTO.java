package com.healthcare.appointmentsystem.dto;

import com.healthcare.appointmentsystem.model.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter

public class AppointmentResponseDTO {
    private Long appointmentId;
    private AppointmentStatus status;
    private String notes;
    private String reason;
    private DoctorDTO doctor;
    private PatientDTO patient;
    private LocalDateTime appointmentDateTime;
}
