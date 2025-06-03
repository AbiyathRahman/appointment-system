package com.healthcare.appointmentsystem.controller;

import com.healthcare.appointmentsystem.dto.UserResponseDTO;
import com.healthcare.appointmentsystem.model.User;
import com.healthcare.appointmentsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserResponseDTO response = UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getUserRole().name())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
                
        return ResponseEntity.ok(response);
    }
}