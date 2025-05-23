package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.AppointmentRequestDTO;
import com.healthcare.appointmentsystem.dto.AppointmentResponseDTO;
import com.healthcare.appointmentsystem.dto.DoctorDTO;
import com.healthcare.appointmentsystem.mapper.AppointmentMapper;
import com.healthcare.appointmentsystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, AppointmentMapper appointmentMapper) {
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
    }
    // Create appointment
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@RequestBody AppointmentRequestDTO requestDTO){
        var savedAppointment = appointmentMapper.toEntity(requestDTO);
        var appointment = appointmentService.createAppointment(savedAppointment);
        var responseDTO = appointmentMapper.toResponseDTO(appointment);
        return ResponseEntity.ok(responseDTO);
    }
    // Find appointment by ID
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id){
        var appointment = appointmentService.findAppointmentById(id);
        if(appointment != null){
            var responseDTO = appointmentMapper.toResponseDTO(appointment);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.notFound().build();
    }
    // Update Appointment by ID
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequestDTO requestDTO){
        var appointmentToUpdate = appointmentService.findAppointmentById(id);
        if(appointmentToUpdate != null){
            appointmentMapper.updateEntityFromDTO(requestDTO, appointmentToUpdate);
            var appointment = appointmentService.updateAppointment(appointmentToUpdate);
            var responseDTO = appointmentMapper.toResponseDTO(appointment);
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.notFound().build();
    }
    // Delete Appointment By ID
    @DeleteMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> deleteAppointment(@PathVariable Long id) {
        var appointmentToDelete = appointmentService.findAppointmentById(id);
        if (appointmentToDelete != null) {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    // Get all appointments
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        var appointments = appointmentService.findAllAppointments();
        if (appointments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var responseDTOs = appointments.stream()
            .map(appointmentMapper::toResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        var appointmentsByDoctor = appointmentService.findAppointmentByDoctorId(doctorId);
        if(appointmentsByDoctor.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        var responseDTOs = appointmentsByDoctor.stream()
            .map(appointmentMapper::toResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByPatientId(@PathVariable Long patientId){
        var appointmentsByPatient = appointmentService.findAppointmentByPatientId(patientId);
        if(appointmentsByPatient.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        var responseDTOs = appointmentsByPatient.stream()
            .map(appointmentMapper::toResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
}