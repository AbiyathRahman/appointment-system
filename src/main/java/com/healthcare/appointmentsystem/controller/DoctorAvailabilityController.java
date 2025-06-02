package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.AvailabilityRequestDTO;
import com.healthcare.appointmentsystem.dto.AvailabilityResponseDTO;
import com.healthcare.appointmentsystem.mapper.AvailabilityMapper;
import com.healthcare.appointmentsystem.model.Doctor;
import com.healthcare.appointmentsystem.repository.DoctorRepository;
import com.healthcare.appointmentsystem.service.DoctorAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctor/availabilities")
public class DoctorAvailabilityController {

    @Autowired
    private DoctorAvailabilityService availabilityService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AvailabilityMapper availabilityMapper;

    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<AvailabilityResponseDTO> createAvailability(@RequestBody AvailabilityRequestDTO requestDTO){
        Doctor doctor = doctorRepository.findById(requestDTO.getDoctorId()).orElseThrow(()-> new RuntimeException("Doctor not found"));
        var availability = availabilityMapper.toEntity(requestDTO, doctor);
        var savedAvailability = availabilityService.createDoctorAvailability(availability);
        var responseDTO = availabilityMapper.toResponseDTO(savedAvailability);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

    }
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityResponseDTO> updateAvailability(@PathVariable Long id, @RequestBody AvailabilityRequestDTO requestDTO){
        var existingAvailability = availabilityService.findDoctorAvailabilityById(id);
        availabilityMapper.updateEntityFromDTO(requestDTO, existingAvailability);

        var updatedAvailability = availabilityService.updateDoctorAvailability(existingAvailability);
        var responseDTO = availabilityMapper.toResponseDTO(updatedAvailability);
        return ResponseEntity.ok(responseDTO);
    }
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<AvailabilityResponseDTO> deleteAvailabiliy(@PathVariable Long id){
        availabilityService.deleteDoctorAvailability(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityResponseDTO> getAvailabilitiesById(@PathVariable Long id){
        var availability = availabilityService.findDoctorAvailabilityById(id);
        if(availability != null){
            var responseDTO = availabilityMapper.toResponseDTO(availability);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<AvailabilityResponseDTO> getAvailabilitiesByDoctorId(@PathVariable Long doctorId){
        var availabilities = availabilityService.findDoctorAvailabilityByDoctorId(doctorId);
        if(availabilities.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        var responseDTOs = availabilities.stream()
                .map(availabilityMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok((AvailabilityResponseDTO) responseDTOs);
    }
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<AvailabilityResponseDTO> getAvailabilitiesForDate(@PathVariable Long doctorId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        var availabilities = availabilityService.findDoctorAvailabilityByDoctorIdAndSpecificDate(doctorId, date);
        if(availabilities.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        var responseDTOs = availabilities.stream()
                .map(availabilityMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok((AvailabilityResponseDTO) responseDTOs);
    }

    @GetMapping("/doctor/{doctorId}/slots")
    public ResponseEntity<AvailabilityResponseDTO> getAvailableTimeSlots(@PathVariable Long doctorId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        var slots = availabilityService.getAvailableTimeSlots(doctorId, date);
        if(slots.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok((AvailabilityResponseDTO) slots);

    }

}
