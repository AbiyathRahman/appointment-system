package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findDoctorById(Long doctorId);
    List<DoctorAvailability> findDoctorByIdAndDayOfWeek(Long doctorId, DayOfWeek dayOfWeek);
    List<DoctorAvailability> findDoctorByIdAndSpecificDate(Long doctorId, LocalDate date);

    // Find all availabilities for a specific date (combining specific date and day of week)
    @Query("SELECT a FROM DoctorAvailability a WHERE a.doctor.id = :doctorId AND " +
            "(a.specificDate = :date OR (a.specificDate IS NULL AND a.dayOfWeek = :dayOfWeek))")
    List<DoctorAvailability> findAvailabilitiesForDate(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("dayOfWeek") DayOfWeek dayOfWeek);

}
