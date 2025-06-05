package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.UserResponseDTO;
import com.healthcare.appointmentsystem.dto.UserUpdateRequestDTO;
import com.healthcare.appointmentsystem.model.Patient;
import com.healthcare.appointmentsystem.model.User;
import com.healthcare.appointmentsystem.service.PatientService;
import com.healthcare.appointmentsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    @Autowired
    private PatientService patientService;
    
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        System.out.println("Getting current user for: " + username);
        
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("Found user: " + user);
        
        // Get patient data if user is a patient
        Optional<Patient> patientOpt = patientService.findPatientByUserId(user.getId());
        
        UserResponseDTO.UserResponseDTOBuilder builder = UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getUserRole().name())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin());
        
        // Add patient data if available
        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();
            System.out.println("Found patient: " + patient);
            builder.firstName(patient.getFirstName())
               .lastName(patient.getLastName())
               .phone(patient.getPhone())
               .address(patient.getAddress());
        } else {
            System.out.println("No patient record found for user ID: " + user.getId());
        }
        
        UserResponseDTO response = builder.build();
        System.out.println("Returning user response: " + response);
                
        return ResponseEntity.ok(response);
    }
@PutMapping("/me")
public ResponseEntity<?> updateCurrentUser(
        @RequestBody UserUpdateRequestDTO updateRequest, 
        Authentication authentication) {
    
    try {
        String username = authentication.getName();
        System.out.println("Updating user: " + username);
        System.out.println("Update request: " + updateRequest);
        
        User currentUser = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("Current user before update: " + currentUser);
        
        boolean userUpdated = false;
        boolean patientUpdated = false;
        
        // Update email in User table if provided
        if (updateRequest.getEmail() != null && !updateRequest.getEmail().trim().isEmpty()) {
            String newEmail = updateRequest.getEmail().trim();
            
            // Check if email is already taken by another user
            Optional<User> existingUser = userService.findUserByEmail(newEmail);
            if (existingUser.isPresent() && !existingUser.get().getId().equals(currentUser.getId())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is already taken"));
            }
            
            if (!newEmail.equals(currentUser.getEmail())) {
                currentUser.setEmail(newEmail);
                userUpdated = true;
                System.out.println("Updated email to: " + newEmail);
            }
        }
        
        // Find the patient record associated with this user
        Patient currentPatient = patientService.findPatientByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Patient record not found"));
        
        System.out.println("Current patient before update: " + currentPatient);
        
        // Update patient-specific fields
        if (updateRequest.getFirstName() != null && !updateRequest.getFirstName().trim().isEmpty()) {
            String newFirstName = updateRequest.getFirstName().trim();
            if (!newFirstName.equals(currentPatient.getFirstName())) {
                currentPatient.setFirstName(newFirstName);
                patientUpdated = true;
                System.out.println("Updated firstName to: " + newFirstName);
            }
        }
        
        if (updateRequest.getLastName() != null && !updateRequest.getLastName().trim().isEmpty()) {
            String newLastName = updateRequest.getLastName().trim();
            if (!newLastName.equals(currentPatient.getLastName())) {
                currentPatient.setLastName(newLastName);
                patientUpdated = true;
                System.out.println("Updated lastName to: " + newLastName);
            }
        }
        
        if (updateRequest.getPhone() != null && !updateRequest.getPhone().trim().isEmpty()) {
            String newPhone = updateRequest.getPhone().trim();
            if (!newPhone.equals(currentPatient.getPhone())) {
                currentPatient.setPhone(newPhone);
                patientUpdated = true;
                System.out.println("Updated phone to: " + newPhone);
            }
        }
        
        if (updateRequest.getAddress() != null && !updateRequest.getAddress().trim().isEmpty()) {
            String newAddress = updateRequest.getAddress().trim();
            if (!newAddress.equals(currentPatient.getAddress())) {
                currentPatient.setAddress(newAddress);
                patientUpdated = true;
                System.out.println("Updated address to: " + newAddress);
            }
        }
        
        // Save updates
        if (userUpdated) {
            System.out.println("Saving user with updates...");
            currentUser = userService.updateUser(currentUser);
            System.out.println("User saved successfully: " + currentUser);
        }
        
        if (patientUpdated) {
            System.out.println("Saving patient with updates...");
            currentPatient = patientService.updatePatient(currentPatient);
            System.out.println("Patient saved successfully: " + currentPatient);
        }
        
        if (!userUpdated && !patientUpdated) {
            System.out.println("No changes detected, skipping update");
        }
        
        // Return the updated user data (combining User and Patient info)
        UserResponseDTO response = UserResponseDTO.builder()
                .id(currentUser.getId())
                .username(currentUser.getUsername())
                .email(currentUser.getEmail())
                .firstName(currentPatient.getFirstName())
                .lastName(currentPatient.getLastName())
                .phone(currentPatient.getPhone())
                .address(currentPatient.getAddress())
                .role(currentUser.getUserRole().name())
                .createdAt(currentUser.getCreatedAt())
                .lastLogin(currentUser.getLastLogin())
                .build();
                
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        // Log the error for debugging
        System.err.println("Error updating user: " + e.getMessage());
        e.printStackTrace();
        
        return ResponseEntity.badRequest().body(Map.of("error", "Failed to update user: " + e.getMessage()));
    }
}
}