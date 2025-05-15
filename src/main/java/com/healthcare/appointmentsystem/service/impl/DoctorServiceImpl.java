package com.healthcare.appointmentsystem.service.impl;

import com.healthcare.appointmentsystem.model.Doctor;
import com.healthcare.appointmentsystem.repository.DoctorRepository;
import com.healthcare.appointmentsystem.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }
    @Override
    public Doctor createDoctor(Doctor doctor) {
        if(doctor == null){
            throw new RuntimeException("Doctor cannot be null");
        }
        if(doctorRepository.existsByLicenseNumber(doctor.getLicenseNumber())){
            throw new RuntimeException("License number already exists");
        }
        return doctorRepository.save(doctor);
    }
    @Override
    public Doctor updateDoctor(Doctor doctor) {
        if(!doctorRepository.existsById(doctor.getId())){
            throw new RuntimeException("Doctor not found");
        }

        return doctorRepository.save(doctor);
    }
    @Override
    public void deleteDoctor(Long iD){
        if(!doctorRepository.existsById(iD)){
            throw new RuntimeException("Doctor not found");
        }
        doctorRepository.deleteById(iD);
    }

    @Override
    public Optional<Doctor> findDoctorById(Long id){
        return doctorRepository.findById(id);
    }

    @Override
    public Optional<Doctor> findDoctorByLicenseNumber(String licenseNumber){
        return doctorRepository.findDoctorByLicenseNumber(licenseNumber);
    }

    @Override
    public List<Doctor> findDoctorByFirstNameAndLastName(String firstName, String lastName){
        return doctorRepository.findDoctorByFirstNameAndLastName(firstName, lastName);

    }

    @Override
    public List<Doctor> findDoctorBySpecialization(String specialization){
        return doctorRepository.findDoctorBySpecialization(specialization);
    }



}
