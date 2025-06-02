package com.healthcare.appointmentsystem.service.impl;

import com.healthcare.appointmentsystem.dto.TimeSlotDTO;
import com.healthcare.appointmentsystem.exception.BadRequestException;
import com.healthcare.appointmentsystem.exception.ConflictException;
import com.healthcare.appointmentsystem.exception.ResourceNotFoundException;
import com.healthcare.appointmentsystem.model.Appointment;
import com.healthcare.appointmentsystem.model.AppointmentStatus;
import com.healthcare.appointmentsystem.model.Doctor;
import com.healthcare.appointmentsystem.model.DoctorAvailability;
import com.healthcare.appointmentsystem.repository.AppointmentRepository;
import com.healthcare.appointmentsystem.repository.DoctorAvailabilityRepository;
import com.healthcare.appointmentsystem.repository.DoctorRepository;
import com.healthcare.appointmentsystem.service.DoctorAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {

    @Autowired
    private DoctorAvailabilityRepository availabilityRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public DoctorAvailability createDoctorAvailability(DoctorAvailability doctorAvailability) {
        if(doctorAvailability == null){
            throw new RuntimeException("Doctor availability cannot be null");

        }
        Doctor doctor = doctorRepository.findById(doctorAvailability.getDoctor().getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctorAvailability.setDoctor(doctor);

        if(doctorAvailability.getStartTime().isAfter(doctorAvailability.getEndTime())){
            throw new ConflictException("Start time cannot be after end time");
        }
        checkForOverlappingAvailability(doctorAvailability);

        return availabilityRepository.save(doctorAvailability);
    }
    @Override
    public DoctorAvailability updateDoctorAvailability(DoctorAvailability doctorAvailability) {
        DoctorAvailability existingAvailability = findDoctorAvailabilityById(doctorAvailability.getId());

        checkForOverlappingAvailability(existingAvailability);

        return availabilityRepository.save(existingAvailability);
    }

    @Override
    public void deleteDoctorAvailability(Long id){
        DoctorAvailability availability = findDoctorAvailabilityById(id);
        availabilityRepository.delete(availability);
    }

    @Override
    public DoctorAvailability findDoctorAvailabilityById(Long id){
        return availabilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor availability not found"));
    }

    @Override
    public List<DoctorAvailability> findDoctorAvailabilityByDoctorId(Long doctorId){
        if(!doctorRepository.existsById(doctorId)){
            throw new ResourceNotFoundException("Doctor","id",doctorId);

        }
        return availabilityRepository.findDoctorById(doctorId);
    }

    @Override
    public List<DoctorAvailability> findDoctorAvailabilityByDoctorIdAndDayOfWeek(Long doctorId, DayOfWeek date){
        if(!doctorRepository.existsById(doctorId)){
            throw new ResourceNotFoundException("Doctor","id",doctorId);
        }
        return availabilityRepository.findDoctorByIdAndDayOfWeek(doctorId, date);
    }

    @Override
    public List<DoctorAvailability> findDoctorAvailabilityByDoctorIdAndSpecificDate(Long doctorId, LocalDate date){
        if(!doctorRepository.existsById(doctorId)){
            throw new ResourceNotFoundException("Doctor","id",doctorId);
        }
        return availabilityRepository.findDoctorByIdAndSpecificDate(doctorId, date);
    }
    @Override
    public boolean isTimeSlotAvailable(Long doctorId, LocalDate date, LocalTime time){
        List<DoctorAvailability> doctorAvailabilities = findDoctorAvailabilityByDoctorIdAndSpecificDate(doctorId, date);
        return doctorAvailabilities.stream().anyMatch(a -> a.isAvailable() && a.containsTime(time));
    }

    @Override
    public List<TimeSlotDTO> getAvailableTimeSlots(Long doctorID, LocalDate date){
        Doctor doctor = doctorRepository.findById(doctorID)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        List<DoctorAvailability> availabilities = findDoctorAvailabilityByDoctorIdAndSpecificDate(doctorID, date);

        List <Appointment> appointments = appointmentRepository.findAppointmentByDoctorId(doctorID)
                        .stream()
                        .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                        .toList();
        List<TimeSlotDTO> slots = new ArrayList<>();
        for (DoctorAvailability availability : availabilities) {
            if(!availability.isAvailable()){
                continue;
            }
            // Generate time slots
            LocalTime currentTime = availability.getStartTime();
            int slotDuration = availability.getSlotDuration();
            while(currentTime.plusMinutes(slotDuration).isBefore(availability.getEndTime()) ||
            currentTime.plusMinutes(slotDuration).equals(availability.getEndTime())){

                LocalDateTime slotStart = LocalDateTime.of(date, currentTime);
                LocalDateTime slotEnd = slotStart.plusMinutes(slotDuration);
                boolean isAvailable = true;
                for(Appointment appointment : appointments){
                    LocalDateTime appointmentStart = appointment.getAppointmentDateTime();
                    LocalDateTime appointmentEnd = appointment.getEndDateTime();
                    if((slotStart.isBefore(appointmentStart) || slotStart.equals(appointmentEnd)) &&
                            (slotEnd.isAfter(appointmentStart) || slotEnd.equals(appointmentStart))){
                        isAvailable = false;
                        break;

                    }
                }
                if(isAvailable){
                    TimeSlotDTO slot = TimeSlotDTO.builder()
                            .startTime(LocalDate.from(slotStart))
                            .endTime(LocalDate.from(slotEnd))
                            .available(true)
                            .doctorId(doctorID)
                            .doctorName(doctor.getFirstName() + " " + doctor.getLastName())
                            .build();
                    slots.add(slot);
                }
                currentTime = currentTime.plusMinutes(slotDuration);
            }
        }
        return slots.stream()
                .sorted(Comparator.comparing(TimeSlotDTO::getStartTime)).collect(Collectors.toList());



    }
    /**
     * Checks if a doctor is available at a specific date and time
     *
     * @param doctorId The ID of the doctor
     * @param date The date to check availability for
     * @param time The specific time to check
     * @return true if the doctor is available, false otherwise
     */
    @Override
    public boolean isDoctorAvailableAt(Long doctorId, LocalDate date, LocalTime time) {
        if (doctorId == null || date == null || time == null) {
            throw new BadRequestException("Doctor ID, date, and time cannot be null");
        }

        // Get the day of week from the date
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Find all availabilities for this doctor on this date (specific date entries or recurring weekly entries)
        List<DoctorAvailability> availabilities = availabilityRepository.findAvailabilitiesForDate(
                doctorId, date, dayOfWeek);

        // If no availabilities found, doctor is not available
        if (availabilities.isEmpty()) {
            return false;
        }

        // Check if the requested time falls within any of the doctor's available time slots
        return availabilities.stream().anyMatch(availability ->
                time.equals(availability.getStartTime()) || time.equals(availability.getEndTime()) ||
                        (time.isAfter(availability.getStartTime()) && time.isBefore(availability.getEndTime()))
        );
    }
   private void checkForOverlappingAvailability(DoctorAvailability doctorAvailability){
        List<DoctorAvailability> existingAvailability;

        if(doctorAvailability.getSpecificDate() != null){
            // Check specific date availabilities
            existingAvailability = findDoctorAvailabilityByDoctorIdAndSpecificDate(
                    doctorAvailability.getDoctor().getId(), doctorAvailability.getSpecificDate());
        }else if(doctorAvailability.getDayOfWeek() != null){
            existingAvailability = availabilityRepository.findDoctorByIdAndDayOfWeek(
                    doctorAvailability.getDoctor().getId(), doctorAvailability.getDayOfWeek()
            );
        }else{
            throw new ConflictException("Doctor availability must have either specific date or day of week");
        }
        // Remove current availabiltiy from checks if its an update
        if (doctorAvailability.getId() != null) {
            existingAvailability = existingAvailability.stream()
                    .filter(a -> !a.getId().equals(doctorAvailability.getId()))
                    .collect(Collectors.toList());
        }
        for(DoctorAvailability existing : existingAvailability){
            if(isTimeOverlapping(doctorAvailability.getStartTime(), doctorAvailability.getEndTime(),
            existing.getStartTime(), existing.getEndTime())){
                throw new ConflictException("Doctor availability overlaps with another availability");
            }
        }
   }
   private boolean isTimeOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2){
        return(start1.isBefore(end2) || start1.equals(end2)) && (end1.isAfter(start2) || end1.equals(start2));
   }
}
