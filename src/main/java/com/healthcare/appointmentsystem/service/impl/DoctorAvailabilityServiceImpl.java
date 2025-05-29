package com.healthcare.appointmentsystem.service.impl;

import com.healthcare.appointmentsystem.dto.TimeSlotDTO;
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
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
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
        List<DoctorAvailability> doctorAvailabilities = findDoctorAvailabilityByDoctorIdAndSpecificDate(doctorID, date);

        List <Appointment> appointments = appointmentRepository.findAppointmentByDoctorId(doctorID)
                        .stream()
                        .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .collect(Collectors.toList());



    }
}
