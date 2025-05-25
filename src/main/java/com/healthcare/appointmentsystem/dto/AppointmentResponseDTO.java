package com.healthcare.appointmentsystem.dto;
package com.healthcare.appointmentsystem.dto;

import com.healthcare.appointmentsystem.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDTO {
    private Long id;
    private DoctorDTO doctor;
    private PatientDTO patient;
    private LocalDateTime appointmentDateTime;
    private LocalDateTime endDateTime;
    private AppointmentStatus status;
    private String reason;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
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
    private String doctorName;
    private String doctorSpecialization;
    private String patientName;
    private LocalDateTime appointmentDateTime;
}
