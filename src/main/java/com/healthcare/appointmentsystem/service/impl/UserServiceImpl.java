package com.healthcare.appointmentsystem.service.impl;

import com.healthcare.appointmentsystem.model.User;
import com.healthcare.appointmentsystem.repository.UserRepository;
import com.healthcare.appointmentsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}