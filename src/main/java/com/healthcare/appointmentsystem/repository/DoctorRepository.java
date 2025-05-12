package com.healthcare.appointmentsystem.repository;

import com.healthcare.appointmentsystem.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findDoctorByLicenseNumber(String licenseNumber);
    List<Doctor> findDoctorBySpecialization(String specialization);
    List<Doctor> findDoctorByFirstName(String firstName);
    List<Doctor> findDoctorByLastName(String lastName);
    List<Doctor> findDoctorByFirstNameAndLastName(String firstName, String lastName);
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsBySpecialization(String specialization);
    boolean existsByFirstName(String firstName);
    boolean existsByLastName(String lastName);

}
