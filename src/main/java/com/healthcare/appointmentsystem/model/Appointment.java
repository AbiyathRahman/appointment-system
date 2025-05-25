package com.healthcare.appointmentsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    // Relation to Doctors
    @NotNull(message = "Doctor is required")
    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id", nullable = false)
    private Doctor doctor;
    
    // Relation to Patients
    @NotNull(message = "Patient is required")
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;
    
    // Appointment Information
    @NotNull(message = "Appointment date and time is required")
    @FutureOrPresent(message = "Appointment date must be in the present or future")
    @Column(name = "appointment_date_time", nullable = false)
    private LocalDateTime appointmentDateTime;
    
    @Column(name = "end_time")
    private LocalDateTime endDateTime;
    
    @NotNull(message = "Appointment status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @NotBlank(message = "Reason is required")
    @Size(min = 3, max = 255, message = "Reason must be between 3 and 255 characters")
    @Column(nullable = false)
    private String reason;
    
    // Created, updated and duration
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    private static final int DEFAULT_DURATION = 30;

    public Appointment() {}

    public Appointment(Doctor doctor, Patient patient, LocalDateTime appointmentDateTime, AppointmentStatus status, String reason) {
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentDateTime = appointmentDateTime;
        this.status = status;
        this.reason = reason;
    }
    
    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Use default end time to figure out the end date
    public void setAppointmentDateTime(LocalDateTime startTime) {
        this.appointmentDateTime = startTime;
        if(startTime != null){
            this.endDateTime = startTime.plusMinutes(DEFAULT_DURATION);
            this.status = AppointmentStatus.SCHEDULED;
        }
    }
    
    // Use custom duration to figure out the end time
    public void scheduleAppointment(LocalDateTime startTime, int duration) {
        this.appointmentDateTime = startTime;
        if(startTime != null){
            this.endDateTime = startTime.plusMinutes(duration);
        }
        this.status = AppointmentStatus.SCHEDULED;
    }
}
