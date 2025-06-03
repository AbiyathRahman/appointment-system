package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.model.Doctor;
import com.healthcare.appointmentsystem.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    
    @Autowired
    private DoctorService doctorService;
    
    @GetMapping
    public ResponseEntity<List<Map<String, ? extends Serializable>>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        
        // Convert to the format your frontend expects
        List<Map<String, ? extends Serializable>> doctorList = doctors.stream()
                .map(doctor -> Map.of(
                    "id", doctor.getId(),
                    "firstName", doctor.getFirstName(),
                    "lastName", doctor.getLastName(),
                    "specialization", doctor.getSpecialization(),
                    "licenseNumber", doctor.getLicenseNumber(),
                    "email", doctor.getUser() != null ? doctor.getUser().getEmail() : ""
                ))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(doctorList);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDoctorById(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        Map<String, Object> doctorData = Map.of(
            "id", doctor.getId(),
            "firstName", doctor.getFirstName(),
            "lastName", doctor.getLastName(),
            "specialization", doctor.getSpecialization(),
            "licenseNumber", doctor.getLicenseNumber(),
            "email", doctor.getUser() != null ? doctor.getUser().getEmail() : ""
        );
        
        return ResponseEntity.ok(doctorData);
    }
}