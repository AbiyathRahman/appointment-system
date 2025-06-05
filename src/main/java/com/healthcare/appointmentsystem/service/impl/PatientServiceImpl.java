package com.healthcare.appointmentsystem.service.impl;

import com.healthcare.appointmentsystem.model.Patient;
import com.healthcare.appointmentsystem.repository.PatientRepository;
import com.healthcare.appointmentsystem.service.PatientService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Patient createPatient(Patient patient) {
        if(patientRepository.existsByFirstName(patient.getFirstName())){
            throw new RuntimeException("First name already exists");
        }
        if(patientRepository.existsByLastName(patient.getLastName())){
            throw new RuntimeException("Last name already exists");
        }
        return patientRepository.save(patient);
    }
    @Override
    @Transactional
    public Patient updatePatient(Patient patient) {
        if(!patientRepository.existsById(patient.getId())){
            throw new RuntimeException("Patient not found");
        }
        return patientRepository.save(patient);
    }
    @Override
    public void deletePatient(Long Id){
        if(!patientRepository.existsById(Id)){
            throw new RuntimeException("Patient not found");
        }
        patientRepository.deleteById(Id);
    }
    @Override
    public Optional<Patient> findPatientById(Long id){
        return patientRepository.findById(id);
    }
    @Override
    public List<Patient> findPatientByFirstName(String firstName){
        return patientRepository.findPatientByFirstName(firstName);
    }
    @Override
    public List<Patient> findPatientByLastName(String lastName){
        return patientRepository.findPatientByLastName(lastName);
    }
    @Override
    public Optional<Patient> findPatientByFirstNameAndLastName(String firstName, String lastName){
        return patientRepository.findPatientByFirstNameAndLastName(firstName, lastName);
    }
    @Override
    public Optional<Patient> findPatientByUserId(Long userId) {
        return patientRepository.findByUserId(userId);
    }

}
