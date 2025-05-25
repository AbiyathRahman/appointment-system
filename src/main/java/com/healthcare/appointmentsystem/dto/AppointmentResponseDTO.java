package com.healthcare.appointmentsystem.dto;
import com.healthcare.appointmentsystem.model.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppointmentResponseDTO {
    private Long appointmentId;
    private String doctorName;
    private String patientName;
    private String doctorSpecialization;
    private LocalDateTime appointmentDateTime;
    private LocalDateTime endDateTime;
    private AppointmentStatus status;
    private String reason;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

