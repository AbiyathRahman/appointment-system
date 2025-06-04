package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.AuthenticationResponseDTO;
import com.healthcare.appointmentsystem.dto.LoginRequestDTO;
import com.healthcare.appointmentsystem.dto.RegistrationRequestDTO;
import com.healthcare.appointmentsystem.exception.BadRequestException;
import com.healthcare.appointmentsystem.model.Gender;
import com.healthcare.appointmentsystem.model.Patient;
import com.healthcare.appointmentsystem.model.Role;
import com.healthcare.appointmentsystem.model.User;
import com.healthcare.appointmentsystem.repository.PatientRepository;
import com.healthcare.appointmentsystem.repository.UserRepository;
import com.healthcare.appointmentsystem.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PatientRepository patientRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequestDTO loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())

        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(()-> new BadRequestException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok(new AuthenticationResponseDTO(
                jwt,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole().name()
        ));
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody RegistrationRequestDTO registerRequest){
        if(userRepository.existsByUsername(registerRequest.getUsername())){
            throw new BadRequestException("Username is already taken");
        }
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new BadRequestException("Email is already in use");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setUserRole(Role.ROLE_PATIENT); // Default Role

        userRepository.save(user);

        Patient patient = new Patient();
        patient.setUser(user);
        patient.setFirstName(registerRequest.getFirstName());
        patient.setLastName(registerRequest.getLastName());
        patient.setGender(Gender.MALE);
        patient.setBirthDate(LocalDate.of(1985, 5, 15));
        patientRepository.save(patient);



        return ResponseEntity.ok("User registered successfully!");
    }

}
