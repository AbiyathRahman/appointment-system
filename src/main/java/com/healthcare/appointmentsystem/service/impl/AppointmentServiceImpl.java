package com.healthcare.appointmentsystem.service.impl;

import com.healthcare.appointmentsystem.model.Appointment;
import com.healthcare.appointmentsystem.model.AppointmentStatus;
import com.healthcare.appointmentsystem.repository.AppointmentRepository;
import com.healthcare.appointmentsystem.repository.DoctorRepository;
import com.healthcare.appointmentsystem.repository.PatientRepository;
import com.healthcare.appointmentsystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public Appointment createAppointment(Appointment appointment){
        doctorRepository.findById(appointment.getDoctor().getId()).orElseThrow(() -> new RuntimeException("Doctor Not Found"));
        patientRepository.findById(appointment.getPatient().getId()).orElseThrow(() -> new RuntimeException("Patient Not Found"));
        //Validate time slot
        if(!isTimeSlotAvailable(appointment.getDoctor().getId(), appointment.getAppointmentDateTime().toLocalDate(), 30)){
            throw new RuntimeException("Time slot is not available");
        }
        if(appointment.getStatus() == null){
            appointment.setStatus(AppointmentStatus.SCHEDULED);
        }
        return appointmentRepository.save(appointment);

    }
    @Override
    public Appointment updateAppointment(Appointment appointment){
        if(!appointmentRepository.existsById(appointment.getId())){
            throw new RuntimeException("Appointment not found");
        }
        Appointment appointmentToUpdate = appointmentRepository.findById(appointment.getId()).get();
        appointmentToUpdate.setAppointmentDateTime(appointment.getAppointmentDateTime());
        appointmentToUpdate.setEndDateTime(appointment.getEndDateTime());
        appointmentToUpdate.setReason(appointment.getReason());
        appointmentToUpdate.setNotes(appointment.getNotes());
        appointment.setStatus(appointment.getStatus());
        appointmentToUpdate.setStatus(appointment.getStatus());
        return appointmentRepository.save(appointmentToUpdate);
    }
    @Override
    public Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus status){
        Appointment appointmentToUpdate = findAppointmentById(appointmentId);
        appointmentToUpdate.setStatus(status);
        return appointmentRepository.save(appointmentToUpdate);
    }
    @Override
    public void deleteAppointment(Long appointmentId){
        if(!appointmentRepository.existsById(appointmentId)){
            throw new RuntimeException("Appointment not found");
        }
        appointmentRepository.deleteById(appointmentId);
    }
    @Override
    public Appointment findAppointmentById(Long id){
        return appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));

    }
    @Override
    public Optional<Appointment> findAppointmentByDoctorIdAndPatientId(Long doctorId, Long patientId){
        if(!doctorRepository.existsById(doctorId)){
            throw new RuntimeException("Doctor not found");
        }
        if(!patientRepository.existsById(patientId)){
            throw new RuntimeException("Patient not found");
        }
        return appointmentRepository.findAppointmentByDoctorIdAndPatientId(doctorId, patientId);

    }
    @Override
    public List<Appointment> findAppointmentByDoctorId(Long doctorId){
        return appointmentRepository.findAppointmentByDoctorId(doctorId);
    }

    @Override
    public List<Appointment> findAppointmentByPatientId(Long patientId){
        return appointmentRepository.findAppointmentByPatientId(patientId);
    }
    @Override
    public List<Appointment> findAppointmentByDate(LocalDateTime date){
        return appointmentRepository.findAppointmentByAppointmentDateTime(date.toLocalDate().atStartOfDay(java.time.ZoneId.systemDefault()).toLocalDateTime());
    }
    
    @Override
    public List<Appointment> findAppointmentBetweenDates(LocalDate startDate, LocalDate endDate){
        return appointmentRepository.findAppointmentByAppointmentDateTime(java.time.LocalDateTime.of(startDate, java.time.LocalTime.MIN))
                .stream()
                .filter(appointment -> appointment.getAppointmentDateTime().toLocalDate().isAfter(startDate) && appointment.getAppointmentDateTime().toLocalDate().isBefore(endDate))
                .toList();
    }
    @Override
    public boolean isTimeSlotAvailable(Long doctorId, LocalDate date, int duration){
        List<Appointment> appointments = findAppointmentByDoctorIdAndPatientId(doctorId, null).stream().filter(appointment -> appointment.getAppointmentDateTime().toLocalDate().equals(date)).toList();
        for(Appointment appointment : appointments){
            if(appointment.getEndDateTime().toLocalDate().equals(date)){
                return false;
            }
        }
        return appointments.size() < duration;
    }
    @Override
public boolean hasConflict(Appointment appointment) {
    // Get all appointments for the same doctor on the same date
    List<Appointment> doctorAppointments = findAppointmentByDoctorId(appointment.getDoctor().getId())
        .stream()
        .filter(existingAppointment -> 
            // Only check non-cancelled appointments
            existingAppointment.getStatus() != AppointmentStatus.CANCELLED &&
            // Exclude the current appointment if we're updating
            !Objects.equals(existingAppointment.getId(), appointment.getId()) &&
            // Only check appointments on the same date
            existingAppointment.getAppointmentDateTime().toLocalDate()
                .equals(appointment.getAppointmentDateTime().toLocalDate())
        )
        .toList();

    // Check for time overlaps
    return doctorAppointments.stream().anyMatch(existingAppointment -> 
        // New appointment starts during an existing appointment
        (appointment.getAppointmentDateTime().isAfter(existingAppointment.getAppointmentDateTime()) &&
         appointment.getAppointmentDateTime().isBefore(existingAppointment.getEndDateTime())) ||
        // New appointment ends during an existing appointment
        (appointment.getEndDateTime().isAfter(existingAppointment.getAppointmentDateTime()) &&
         appointment.getEndDateTime().isBefore(existingAppointment.getEndDateTime())) ||
        // New appointment completely contains an existing appointment
        (appointment.getAppointmentDateTime().isBefore(existingAppointment.getAppointmentDateTime()) &&
         appointment.getEndDateTime().isAfter(existingAppointment.getEndDateTime())) ||
        // Exact same start time
        appointment.getAppointmentDateTime().equals(existingAppointment.getAppointmentDateTime())
    );
}

@Override
public List<Appointment> findAllAppointments() {
    return appointmentRepository.findAll();
}
}