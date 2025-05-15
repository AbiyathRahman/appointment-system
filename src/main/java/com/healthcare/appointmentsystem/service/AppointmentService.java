package com.healthcare.appointmentsystem.service;

import com.healthcare.appointmentsystem.model.Appointment;
import com.healthcare.appointmentsystem.model.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment createAppointment(Appointment appointment);
    Appointment updateAppointment(Appointment appointment);
    Appointment updateAppointmentStatus(Long appointmentId, AppointmentStatus status);
    void deleteAppointment(Long appointmentId);
    Appointment findAppointmentById(Long id);
    Optional<Appointment> findAppointmentByDoctorIdAndPatientId(Long doctorId, Long patientId);
    List<Appointment> findAppointmentByDoctorId(Long doctorId);
    List<Appointment> findAppointmentByPatientId(Long patientId);
    List<Appointment> findAppointmentByDate(LocalDate date);
    List<Appointment> findAppointmentBetweenDates(LocalDate startDate, LocalDate endDate);
    boolean isTimeSlotAvailable(Long doctorId, LocalDate date, int duration);
    boolean hasConflict(Appointment appointment);

}
