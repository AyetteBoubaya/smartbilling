package com.smartbilling.smartbilling.auth.service;

import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(User user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }



}
