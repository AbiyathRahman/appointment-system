package com.healthcare.appointmentsystem.dto;

import lombok.Data;

@Data
public class RegistrationRequestDTO {
    private String username;
    private String password;
    private String email;

}
