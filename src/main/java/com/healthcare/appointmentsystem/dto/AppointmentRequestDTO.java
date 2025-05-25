package com.healthcare.appointmentsystem.dto;

import com.healthcare.appointmentsystem.model.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentRequestDTO {


    private Long doctorId;
    

    private Long patientId;
    

    private LocalDateTime appointmentDateTime;
    

    private String reason;
    

    private String notes;
    
    private AppointmentStatus status;
}
