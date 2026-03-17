package com.smartbilling.smartbilling.auth.service;

import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(User user) {
        User exiting = userRepository.findByEmail(user.getEmail())
                .orElseThrow(()->new RuntimeException("Ussr not found"));
        userRepository.delete(exiting);
        System.out.println("User deleted successfully");
    }

    @Override
    public User updateUser(User user) {
        User existing = userRepository.findByEmail(user.getEmail())
                .orElseThrow(()->new RuntimeException("Ussr not found"));
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());
        return userRepository.save(existing);
    }

    @Override
    public User getUserByEmail(String email) {
        return (userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("Ussr not found")));
    }

}

