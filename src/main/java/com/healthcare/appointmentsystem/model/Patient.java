package com.healthcare.appointmentsystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    // User Information
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(nullable = false)
    private LocalDate birthDate;
    // Contact Information
    private String address;
    private String phone;
    // Medical Information
    @Column(name = "blood_Type")
    @Enumerated(EnumType.STRING)
    private BloodType bloodType;
    @Column(columnDefinition = "TEXT")
    private String allergies;
    @Column(name = "medical_notes", columnDefinition = "TEXT")
    private String medicalnotes;

    // Relation to User
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // Timestamps
    @Column(name = "created_at")
    private LocalDate createDate;
    @Column(name = "updated_at")
    private LocalDate updateDate;

    // Constructor
    public Patient() {}

    public Patient(String firstName, String lastName, Gender gender, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    @PrePersist
    protected void onCreate() {
        this.createDate = LocalDate.now();
        this.updateDate = LocalDate.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDate.now();
    }
}
