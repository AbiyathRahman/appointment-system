package com.healthcare.appointmentsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "doctors")
@Getter
@Setter
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Personal Information
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String specialization;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;
    @Column(columnDefinition = "TEXT")
    private String qualification;
    @Column(name = "office_location")
    private String officeLocation;

    // Relation to User
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // Created and Updated at
    @Column(name = "created_at")
    private LocalDate createDate;
    @Column(name = "updated_at")
    private LocalDate updateDate;

    public Doctor() {}
    public Doctor(String firstName, String lastName, String specialization, String licenseNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
    }
    @PrePersist
    private void onCreate() {
        this.createDate = LocalDate.now();
        this.updateDate = LocalDate.now();
    }
    @PreUpdate
    private void onUpdate() {
        this.updateDate = LocalDate.now();
    }
}
