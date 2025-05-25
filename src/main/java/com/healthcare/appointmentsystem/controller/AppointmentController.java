package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.AppointmentRequestDTO;
import com.healthcare.appointmentsystem.dto.AppointmentResponseDTO;
import com.healthcare.appointmentsystem.dto.DoctorDTO;
import com.healthcare.appointmentsystem.mapper.AppointmentMapper;
import com.healthcare.appointmentsystem.model.Appointment;
import com.healthcare.appointmentsystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @GetMapping("/date/{date}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByDate(@PathVariable LocalDateTime date){
        var appointmentsByDate = appointmentService.findAppointmentByDate(date);
        if(appointmentsByDate.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        var responseDTOs = appointmentsByDate.stream()
            .map(appointmentMapper::toResponseDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
    @GetMapping("/range")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentByRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate){
        var appointmentsByRange = appointmentService.findAppointmentBetweenDates(startDate, endDate);
        if(appointmentsByRange.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        var responseDTOs = appointmentsByRange.stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);

    }
    @GetMapping("/doctor/{doctorId}/patient/{patientId}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentByDoctorAndPatient(@PathVariable Long doctorId, @PathVariable Long patientId){
        var appointment = appointmentService.findAppointmentByDoctorIdAndPatientId(doctorId, patientId);
        if(appointment.isPresent()){
            var responseDTO = appointmentMapper.toResponseDTO(appointment.get());
            return ResponseEntity.ok(responseDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateAppointmentStatusById(@PathVariable Long id, @RequestBody AppointmentRequestDTO requestDTO){
        var appointmentToUpdate = appointmentService.findAppointmentById(id);
        if(appointmentToUpdate != null){
            appointmentMapper.updateEntityFromDTO(requestDTO, appointmentToUpdate);
            var appointment = appointmentService.updateAppointmentStatus(id, appointmentToUpdate.getStatus());
            var responseDTO = appointmentMapper.toResponseDTO(appointment);
            return ResponseEntity.ok(responseDTO);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> deleteAppointmentById(@PathVariable Long id){
        var appointmentToDelete = appointmentService.findAppointmentById(id);
        if(appointmentToDelete != null){
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/available")
    public ResponseEntity<List<AppointmentResponseDTO>> getDoctorAvailableAppointments(@RequestParam Long doctorId, @RequestParam LocalDateTime date) {
        var availableAppointments = appointmentService.findAppointmentByDoctorIdAndPatientId(doctorId, null);
        if (availableAppointments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var availableAppointmentsByDate = availableAppointments.stream()
                .filter(appointment -> appointment.getAppointmentDateTime().toLocalDate().equals(date.toLocalDate()))
                .toList();
        if (availableAppointmentsByDate.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            var responseDTOs = availableAppointmentsByDate.stream()
                    .map(appointmentMapper::toResponseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseDTOs);
        }
    }
}