package com.healthcare.appointmentsystem.mapper;

import com.healthcare.appointmentsystem.dto.AvailabilityRequestDTO;
import com.healthcare.appointmentsystem.dto.AvailabilityResponseDTO;
import com.healthcare.appointmentsystem.model.Doctor;
import com.healthcare.appointmentsystem.model.DoctorAvailability;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AvailabilityMapper {

    public DoctorAvailability toEntity(AvailabilityRequestDTO dto, Doctor doctor){
        DoctorAvailability entity = new DoctorAvailability();
        entity.setDoctor(doctor);
        entity.setDayOfWeek(dto.getDayOfWeek());
        entity.setSpecificDate(dto.getSpecificDate());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setSpecificDate(dto.getSpecificDate());
        entity.setNotes(dto.getNotes());
        if(dto.getSlotDuration() != null){
            entity.setSlotDuration(dto.getSlotDuration());
        }
        return entity;
    }

    public void updateEntityFromDTO(AvailabilityRequestDTO dto, DoctorAvailability entity){
        if(dto.getDayOfWeek() != null){
            entity.setDayOfWeek(dto.getDayOfWeek());
        }
        if(dto.getSpecificDate() != null){
            entity.setSpecificDate(dto.getSpecificDate());
        }
        if(dto.getStartTime() != null){
            entity.setStartTime(dto.getStartTime());
        }
        if(dto.getEndTime() != null){
            entity.setEndTime(dto.getEndTime());
        }
        entity.setAvailable(dto.isAvailable());

        if(dto.getSlotDuration() != null){
            entity.setSlotDuration(dto.getSlotDuration());
        }
        if(dto.getNotes() != null){
            entity.setNotes(dto.getNotes());
        }
        if(dto.getSlotDuration() != null){
            entity.setSlotDuration(dto.getSlotDuration());
        }


    }
    public AvailabilityResponseDTO toResponseDTO(DoctorAvailability entity) {
        return AvailabilityResponseDTO.builder()
                .id(entity.getId())
                .doctorId(entity.getDoctor().getId())
                .doctorName(entity.getDoctor().getFirstName() + " " + entity.getDoctor().getLastName())
                .dayOfWeek(entity.getDayOfWeek())
                .specificDate(entity.getSpecificDate())
                .startTime(LocalDate.from(entity.getStartTime()))
                .endTime(LocalDate.from(entity.getEndTime()))
                .available(entity.isAvailable())
                .notes(entity.getNotes())
                .slotDuration(entity.getSlotDuration())
                .build();
    }

}
