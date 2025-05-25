package com.healthcare.appointmentsystem.service.impl;

import com.healthcare.appointmentsystem.exception.BadRequestException;
import com.healthcare.appointmentsystem.exception.ConflictException;
import com.healthcare.appointmentsystem.exception.ResourceNotFoundException;
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
    public Appointment createAppointment(Appointment appointment) {
        // Validate doctor exists
        doctorRepository.findById(appointment.getDoctor().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", appointment.getDoctor().getId()));
        
        // Validate patient exists
        patientRepository.findById(appointment.getPatient().getId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", appointment.getPatient().getId()));
        
        // Validate appointment datetime
        if (appointment.getAppointmentDateTime() == null) {
            throw new BadRequestException("Appointment date and time cannot be null");
        }
        
        // Check if appointment time is in the past
        if (appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Appointment cannot be scheduled in the past");
        }
        
        // Validate timeslot availability
        if (!isTimeSlotAvailable(appointment.getDoctor().getId(), 
                                 appointment.getAppointmentDateTime().toLocalDate(), 30)) {
            throw new ConflictException("Time slot is not available for this doctor");
        }
        
        // Check for conflicts with existing appointments
        if (hasConflict(appointment)) {
            throw new ConflictException("This appointment conflicts with an existing appointment");
        }
        
        // Set default status if not provided
        if (appointment.getStatus() == null) {
            appointment.setStatus(AppointmentStatus.SCHEDULED);
        }
        
        return appointmentRepository.save(appointment);
    }
    
    @Override
    public Appointment updateAppointment(Appointment appointment) {
        // Validate appointment exists
        if (!appointmentRepository.existsById(appointment.getId())) {
            throw new ResourceNotFoundException("Appointment", "id", appointment.getId());
        }
        
        Appointment appointmentToUpdate = appointmentRepository.findById(appointment.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointment.getId()));
        
        // Check for conflicts with other appointments
        if (hasConflict(appointment)) {
            throw new ConflictException("This appointment update conflicts with an existing appointment");
        }
        
        appointmentToUpdate.setAppointmentDateTime(appointment.getAppointmentDateTime());
        appointmentToUpdate.setEndDateTime(appointment.getEndDateTime());
        appointmentToUpdate.setReason(appointment.getReason());
        appointmentToUpdate.setNotes(appointment.getNotes());
        appointmentToUpdate.setStatus(appointment.getStatus());
        
        return appointmentRepository.save(appointmentToUpdate);
    }
    
    @Override
    public Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus status) {
        Appointment appointmentToUpdate = findAppointmentById(appointmentId);
        
        // Validate status transition
        if (appointmentToUpdate.getStatus() == AppointmentStatus.CANCELLED && 
            status != AppointmentStatus.CANCELLED) {
            throw new BadRequestException("Cannot change status of a cancelled appointment");
        }
        
        appointmentToUpdate.setStatus(status);
        return appointmentRepository.save(appointmentToUpdate);
    }
    
    @Override
    public void deleteAppointment(Long appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new ResourceNotFoundException("Appointment", "id", appointmentId);
        }
        appointmentRepository.deleteById(appointmentId);
    }
    
    @Override
    public Appointment findAppointmentById(Long id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
    }
    
    @Override
    public Optional<Appointment> findAppointmentByDoctorIdAndPatientId(Long doctorId, Long patientId) {
        // For the case where patientId is null (available appointments)
        if (patientId == null) {
            if (!doctorRepository.existsById(doctorId)) {
                throw new ResourceNotFoundException("Doctor", "id", doctorId);
            }
            return Optional.empty();
        }
        
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }
        
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }
        
        return appointmentRepository.findAppointmentByDoctorIdAndPatientId(doctorId, patientId);
    }
    
    @Override
    public List<Appointment> findAppointmentByDoctorId(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }
        return appointmentRepository.findAppointmentByDoctorId(doctorId);
    }

    @Override
    public List<Appointment> findAppointmentByPatientId(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }
        return appointmentRepository.findAppointmentByPatientId(patientId);
    }
    
    @Override
    public List<Appointment> findAppointmentByDate(LocalDateTime date) {
        if (date == null) {
            throw new BadRequestException("Date cannot be null");
        }
        return appointmentRepository.findAppointmentByAppointmentDateTime(
            date.toLocalDate().atStartOfDay());
    }
    
    @Override
    public List<Appointment> findAppointmentBetweenDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date cannot be null");
        }
        
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        
        return appointmentRepository.findAppointmentByAppointmentDateTime(LocalDateTime.of(startDate, java.time.LocalTime.MIN))
                .stream()
                .filter(appointment -> 
                    (appointment.getAppointmentDateTime().toLocalDate().isEqual(startDate) || 
                     appointment.getAppointmentDateTime().toLocalDate().isAfter(startDate)) && 
                    (appointment.getAppointmentDateTime().toLocalDate().isEqual(endDate) || 
                     appointment.getAppointmentDateTime().toLocalDate().isBefore(endDate)))
                .toList();
    }
    
    @Override
    public boolean isTimeSlotAvailable(Long doctorId, LocalDate date, int duration) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor", "id", doctorId);
        }
        
        if (date == null) {
            throw new BadRequestException("Date cannot be null");
        }
        
        if (date.isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot check availability for past dates");
        }
        
        List<Appointment> appointments = findAppointmentByDoctorId(doctorId).stream()
            .filter(appointment -> 
                appointment.getStatus() != AppointmentStatus.CANCELLED && 
                appointment.getAppointmentDateTime().toLocalDate().equals(date))
            .toList();
            
        // Check if there are any appointments that would conflict with the new time slot
        for (Appointment appointment : appointments) {
            if (appointment.getEndDateTime().toLocalDate().equals(date)) {
                return false;
            }
        }
        
        // This is a simplistic check; in a real system, you'd have a more sophisticated
        // algorithm considering doctor's working hours, lunch breaks, etc.
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