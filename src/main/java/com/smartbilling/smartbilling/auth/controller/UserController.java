package com.smartbilling.smartbilling.auth.controller;

import com.smartbilling.smartbilling.auth.domain.Role;
import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.dto.requests.UserRequest;
import com.smartbilling.smartbilling.auth.dto.responses.UserResponse;
import com.smartbilling.smartbilling.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // POST /api/users — public
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody @Valid UserRequest request) {
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole(Role.User);

        User saved = userService.createUser(user);
        return toResponse(saved);
    }

    // PUT /api/users/{email} — même user ou Admin
    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('Admin') or authentication.name == #email")
    public UserResponse updateUser(@PathVariable String email,
                                   @RequestBody @Valid UserRequest request) {
        User userToUpdate = new User();
        userToUpdate.setEmail(email);
        userToUpdate.setPassword(request.password());
        userToUpdate.setRole(Role.User);

        User updated = userService.updateUser(userToUpdate);
        return toResponse(updated);
    }

    // DELETE /api/users/{email} — Admin uniquement
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('Admin')")
    public void deleteUser(@PathVariable String email) {
        User userToDelete = new User();
        userToDelete.setEmail(email);
        userService.deleteUser(userToDelete);
    }

    // GET /api/users/{email} — même user ou Admin
    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('Admin') or authentication.name == #email")
    public UserResponse getUser(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return toResponse(user);
    }

    // ── Helper ────────────────────────────────────────────────
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isEmailVerified(),
                user.getCreatedAt()
        );
    }
}