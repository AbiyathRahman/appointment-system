package com.healthcare.appointmentsystem.service;

import com.healthcare.appointmentsystem.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientService {

    Patient createPatient(Patient patient);
    Patient updatePatient(Patient patient);
    void deletePatient(Long patientId);
    Optional<Patient> findPatientById(Long id);
    List<Patient> findPatientByFirstName(String firstName);
    List<Patient> findPatientByLastName(String lastName);
    Optional<Patient> findPatientByFirstNameAndLastName(String firstName, String lastName);
}
