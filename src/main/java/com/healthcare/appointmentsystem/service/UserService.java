package com.healthcare.appointmentsystem.service;

import com.healthcare.appointmentsystem.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // Create User
    User createUser(User user);
    // Update User
    User updateUser(User user);
    // Delete User
    void deleteUser(Long userId);
    // Find User by Id
    Optional<User> findUserById(Long id);
    // Find User by Username
    Optional<User> findUserByUsername(String username);
    // Check if username exists
    boolean existsByUsername(String username);
    // Get a list of all users
    List<User> findAllUsers();
    Optional<User> findUserByEmail(String email);
    boolean existsByEmail(String email);

}
