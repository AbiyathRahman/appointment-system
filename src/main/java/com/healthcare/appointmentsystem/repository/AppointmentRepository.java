package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    // Existing methods...
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND " +
           "a.appointmentDateTime >= :startDateTime AND a.appointmentDateTime < :endDateTime")
    List<Appointment> findAppointmentsByDoctorIdAndDate(
            @Param("doctorId") Long doctorId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
            
    // Add the missing methods
    
    /**
     * Find appointment by doctor ID and patient ID
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.patient.id = :patientId")
    Optional<Appointment> findAppointmentByDoctorIdAndPatientId(
            @Param("doctorId") Long doctorId, 
            @Param("patientId") Long patientId);
    
    /**
     * Find appointments by doctor ID
     */
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId")
    List<Appointment> findAppointmentByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find appointments by patient ID
     */
    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId")
    List<Appointment> findAppointmentByPatientId(@Param("patientId") Long patientId);
    
    /**
     * Find appointments by appointment date time
     * This method finds appointments starting at the specific date time
     */
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime = :dateTime")
    List<Appointment> findAppointmentByAppointmentDateTime(@Param("dateTime") LocalDateTime dateTime);
    
    /**
     * Find appointments between dates
     * This is an additional method that might be useful, combining with the stream filtering you're doing
     */
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime >= :startDate AND a.appointmentDateTime <= :endDate")
    List<Appointment> findAppointmentsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}