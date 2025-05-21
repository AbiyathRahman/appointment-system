package com.healthcare.appointmentsystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorDTO {
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialization;
    private String doctorLicenseNumber;
    private String doctorEmail;
    private String doctorPhoneNumber;
}
