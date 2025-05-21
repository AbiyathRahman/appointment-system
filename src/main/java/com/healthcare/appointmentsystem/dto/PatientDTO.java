package com.healthcare.appointmentsystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientDTO {
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String patientPhoneNumber;
}
