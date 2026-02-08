package com.smartbilling.smartbilling.auth.controller;

import com.smartbilling.smartbilling.auth.domain.Role;
import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.dto.requests.UserRequest;
import com.smartbilling.smartbilling.auth.dto.responses.UserResponse;
import com.smartbilling.smartbilling.auth.repository.UserRepository;
import com.smartbilling.smartbilling.auth.service.UserService;
import com.smartbilling.smartbilling.auth.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {

        this.userService = userService;
        this.userRepository = userRepository;
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

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateUser(@PathVariable String email ,@RequestBody @Valid UserRequest request){
        User userToUpdate = new User();
        userToUpdate.setEmail(email);
        userToUpdate.setPassword(request.password());
        userToUpdate.setRole(Role.User);

        User update = userService.updateUser(userToUpdate);
        return new UserResponse(update.getId(), update.getEmail(), update.getRole());
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String email){
        User userToDelete = new User();
        userToDelete.setEmail(email);

        userService.deleteUser(userToDelete);
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getUser(@PathVariable String email){
        User userToGet = new User();
        userToGet.setEmail(email);
        userToGet.setRole(Role.User);

        userService.getUserByEmail(userToGet.getEmail());
        return new UserResponse(userToGet.getId(), userToGet.getEmail(), userToGet.getRole());
    }
}
