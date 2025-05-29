package com.healthcare.appointmentsystem.service;
import com.healthcare.appointmentsystem.dto.TimeSlotDTO;
import com.healthcare.appointmentsystem.model.DoctorAvailability;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DoctorAvailabilityService {
    DoctorAvailability createDoctorAvailability(DoctorAvailability doctorAvailability);
    DoctorAvailability updateDoctorAvailability(DoctorAvailability doctorAvailability);
    void deleteDoctorAvailability(Long doctorAvailabilityId);
    DoctorAvailability findDoctorAvailabilityById(Long id);
    List<DoctorAvailability> findDoctorAvailabilityByDoctorId(Long doctorId);
    List<DoctorAvailability> findDoctorAvailabilityByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek date);
    List<DoctorAvailability> findDoctorAvailabilityByDoctorIdAndSpecificDate(Long doctorId, LocalDate date);
    boolean isTimeSlotAvailable(Long doctorId, LocalDate date, LocalTime time);
    List<TimeSlotDTO> getAvailableTimeSlots(Long doctorId, LocalDate date);
}
