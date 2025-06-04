package com.healthcare.appointmentsystem.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//@Getter
//@Setter
public class RegistrationRequestDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String username;
    private String password;
    private String email;

}
