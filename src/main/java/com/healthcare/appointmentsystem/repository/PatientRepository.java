package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findPatientByFirstName(String firstName);
    List<Patient> findPatientByLastName(String lastName);
    List<Patient> findPatientByGender(String gender);
    List<Patient> findPatientByFirstNameAndLastName(String firstName, String lastName);
    Optional <Patient> findPatientByEmail(String email);
    Optional <Patient> findPatientByBirthDate(String birthDate);
    boolean existsByFirstName(String firstName);
    boolean existsByLastName(String lastName);
    boolean existsByEmail(String email);
}
