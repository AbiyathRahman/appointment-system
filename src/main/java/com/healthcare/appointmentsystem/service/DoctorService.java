package com.healthcare.appointmentsystem.service;

import com.healthcare.appointmentsystem.model.Doctor;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    Optional<Doctor> getDoctorById(Long id);
    List<Doctor> getDoctorsBySpecialization(String specialization);
    Doctor createDoctor(Doctor doctor);
    Doctor updateDoctor(Doctor doctor);
    void deleteDoctor(Long id);
    boolean existsByLicenseNumber(String licenseNumber);
}