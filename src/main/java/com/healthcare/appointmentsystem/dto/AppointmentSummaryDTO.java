package com.healthcare.appointmentsystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentSummaryDTO {
    private Long appointmentId;
    private String doctorName;
    private String patientName;
    private LocalDateTime appointmentDateTime;
    private String status;
    private String reason;
    private String notes;

}
