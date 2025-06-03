package com.healthcare.appointmentsystem.mapper;

import com.healthcare.appointmentsystem.dto.AppointmentRequestDTO;
import com.healthcare.appointmentsystem.dto.AppointmentResponseDTO;
import com.healthcare.appointmentsystem.dto.AppointmentSummaryDTO;
import com.healthcare.appointmentsystem.dto.DoctorDTO;
import com.healthcare.appointmentsystem.model.Appointment;
import com.healthcare.appointmentsystem.model.Doctor;
import com.healthcare.appointmentsystem.model.Patient;
import com.healthcare.appointmentsystem.service.DoctorService;
import com.healthcare.appointmentsystem.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class AppointmentMapper {
    private final DoctorService doctorService;
    private final PatientService patientService;

    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    public AppointmentMapper(DoctorService doctorService, PatientService patientService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
    }
    public AppointmentResponseDTO toResponseDTO(Appointment appointment){
        if(appointment == null){
            return null;
        }
        // Map appointment info
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setAppointmentId(appointment.getId());
        dto.setStatus(appointment.getStatus());
        dto.setNotes(appointment.getNotes());
        dto.setReason(appointment.getReason());
        dto.setAppointmentDateTime(appointment.getAppointmentDateTime());

        //Map doctor info
        Doctor doctor = appointment.getDoctor();
        if(doctor != null){
            dto.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
            dto.setDoctorSpecialization(doctor.getSpecialization());
        }

        Patient patient = appointment.getPatient();
        if(patient != null){
            dto.setPatientName(patient.getFirstName() + " " + patient.getLastName());



        }
        return dto;
    }

    public AppointmentSummaryDTO toSummaryDTO(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        AppointmentSummaryDTO dto = new AppointmentSummaryDTO();
        dto.setAppointmentId(appointment.getId());
        dto.setReason(appointment.getReason());
        dto.setNotes(appointment.getNotes());
        dto.setStatus(appointment.getStatus().toString());

        // Format datetime as string
        if (appointment.getAppointmentDateTime() != null) {
            dto.setAppointmentDateTime(
                    appointment.getAppointmentDateTime()
            );
        }

        Doctor doctor = appointment.getDoctor();
        if(doctor != null){
            dto.setDoctorName(doctor.getFirstName() + " " + doctor.getLastName());
        }
        Patient patient = appointment.getPatient();
        if(patient != null){
            dto.setPatientName(patient.getFirstName() + " " + patient.getLastName());
        }
        return dto;
        
    }
    
    /**
     * Converts a request DTO to an Appointment entity
     */
    public Appointment toEntity(AppointmentRequestDTO dto){
        if(dto == null){
            return null;
        }
        Appointment appointment = new Appointment();
        
        // Set appointment date time which will also set end time and status
        if(dto.getAppointmentDateTime() != null) {
            appointment.setAppointmentDateTime(dto.getAppointmentDateTime());
        }
        
        // Set basic fields
        appointment.setReason(dto.getReason());
        appointment.setNotes(dto.getNotes());
        
        // Set referenced entities
        if(dto.getDoctorId() != null){
            doctorService.getDoctorById(dto.getDoctorId())
                .ifPresent(appointment::setDoctor);
        }
        if(dto.getPatientId() != null){
            patientService.findPatientById(dto.getPatientId())
                .ifPresent(appointment::setPatient);
        }
        return appointment;
    }
    
    /**
     * Updates an existing entity from DTO data
     */
    public void updateEntityFromDTO(AppointmentRequestDTO dto, Appointment appointment) {
        if(dto == null || appointment == null) {
            return;
        }
        
        // Update appointment date time which will also update end time
        if(dto.getAppointmentDateTime() != null) {
            appointment.setAppointmentDateTime(dto.getAppointmentDateTime());
        }
        
        // Update basic fields
        if(dto.getReason() != null) {
            appointment.setReason(dto.getReason());
        }
        
        if(dto.getNotes() != null) {
            appointment.setNotes(dto.getNotes());
        }
        
        // Update referenced entities
        if(dto.getDoctorId() != null) {
            doctorService.getDoctorById(dto.getDoctorId())
                .ifPresent(appointment::setDoctor);
        }
        
        if(dto.getPatientId() != null) {
            patientService.findPatientById(dto.getPatientId())
                .ifPresent(appointment::setPatient);
        }
    }

}