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
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public User createUser(User user) {
        if(userRepository.existsByUsername(user.getUsername())){
            throw new RuntimeException("Username already exists");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }
    @Override
    public User updateUser(User user) {
        if(!userRepository.existsById(user.getId())){
            throw new RuntimeException("User not found");
        }
        return userRepository.save(user);
    }
    @Override
    public void deleteUser(Long userId){
        if(!userRepository.existsById(userId)){
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }
    @Override
    public Optional<User> findUserById(Long id){
        return userRepository.findById(id);

    }
    @Override
    public Optional<User> findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
    @Override
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
