package com.healthcare.appointmentsystem.model;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(name = "doctor_availabilities")
@Getter
@Setter
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id", nullable = false)
    private Doctor doctor;

    @Column(columnDefinition = "day_of_week_enum")
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalDate specificDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean available = true;

    @Column(name = "slot_duration")
    private int slotDuration = 30;

    @Column(name = "created_at")
    private LocalDate createDate;
    @Column(name = "updated_at")
    private LocalDate updateDate;

    @PrePersist
    private void onCreate() {
        this.createDate = LocalDate.now();
        this.updateDate = LocalDate.now();
    }
    @PreUpdate
    private void onUpdate() {
        this.updateDate = LocalDate.now();
    }
    public boolean appliesToDate(LocalDate date){
        if(specificDate != null){
            return specificDate.equals(date);
        }else if(dayOfWeek != null){
            return dayOfWeek == date.getDayOfWeek();
        }
        return false;
    }

    // Check if time falls within this availablity's hour
    public boolean containsTime(LocalTime time){return !time.isBefore(startTime) && !time.isAfter(endTime);}

}
