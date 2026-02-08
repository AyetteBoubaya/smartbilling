package com.smartbilling.smartbilling.auth.service;

import com.smartbilling.smartbilling.auth.domain.User;

public interface UserService {

    User createUser(User user);
    void deleteUser(User user);
    User updateUser(User user);
    User getUserByEmail(String email);
}
