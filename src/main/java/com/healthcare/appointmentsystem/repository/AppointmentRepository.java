package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.model.Appointment;
import com.healthcare.appointmentsystem.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAppointmentByDoctorId(Long doctorId);
    List<Appointment> findAppointmentByPatientId(Long patientId);
    List<Appointment> findAppointmentByStatus(AppointmentStatus status);
    List<Appointment> findAppointmentByAppointmentDateTime(LocalDateTime appointmentDateTime);
    boolean existsByDoctorId(Long doctorId);
    boolean existsByPatientId(Long patientId);
    boolean existsByStatus(AppointmentStatus status);
    boolean existsByAppointmentDateTime(LocalDateTime appointmentDateTime);

    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);
    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    //List<Appointment> findByDateTimeBetween(LocalDateTime startTime, LocalDate endTime);
}
