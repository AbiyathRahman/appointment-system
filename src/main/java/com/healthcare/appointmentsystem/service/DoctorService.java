package com.healthcare.appointmentsystem.service;

import com.healthcare.appointmentsystem.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorService {
    Doctor createDoctor(Doctor doctor);
    Doctor updateDoctor(Doctor doctor);
    void deleteDoctor(Long doctorId);
    Optional<Doctor> findDoctorById(Long id);
    Optional<Doctor> findDoctorByLicenseNumber(String licenseNumber);
    List<Doctor> findDoctorByFirstNameAndLastName(String firstName, String lastName);
    List<Doctor> findDoctorBySpecialization(String specialization);
}
