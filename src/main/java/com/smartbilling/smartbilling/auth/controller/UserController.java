package com.smartbilling.smartbilling.auth.controller;

import com.smartbilling.smartbilling.auth.domain.Role;
import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.dto.requests.UserRequest;
import com.smartbilling.smartbilling.auth.dto.responses.UserResponse;
import com.smartbilling.smartbilling.auth.service.UserService;
import com.smartbilling.smartbilling.auth.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody @Valid UserRequest request){
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole(Role.User); // default role

        User saved = userService.createUser(user); // <-- User
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getRole());
    }

}
