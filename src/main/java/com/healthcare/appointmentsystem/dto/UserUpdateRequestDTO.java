package com.healthcare.appointmentsystem.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Setter
@Getter
public class UserUpdateRequestDTO
{
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private String email;
}
