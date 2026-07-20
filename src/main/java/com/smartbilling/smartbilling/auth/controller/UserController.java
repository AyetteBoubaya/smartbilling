package com.smartbilling.smartbilling.auth.controller;

import com.smartbilling.smartbilling.auth.domain.Role;
import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.dto.requests.UserRequest;
import com.smartbilling.smartbilling.auth.dto.responses.ErrorResponse;
import com.smartbilling.smartbilling.auth.dto.responses.UserResponse;
import com.smartbilling.smartbilling.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Gestion des comptes utilisateurs (CRUD)")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un utilisateur", description = "Endpoint public — crée un utilisateur avec le rôle User par défaut.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Utilisateur créé",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public UserResponse createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                            {"email": "john@example.com", "password": "secret123"}
                            """)))
            @RequestBody @Valid UserRequest request) {
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setRole(Role.User);
        User saved = userService.createUser(user);
        return toResponse(saved);
    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('Admin') or authentication.name == #email")
    @Operation(
            summary = "Mettre à jour un utilisateur",
            description = "Accessible par l'utilisateur lui-même ou un Admin. Encode le nouveau mot de passe."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé — pas le bon utilisateur"),
            @ApiResponse(responseCode = "400", description = "Utilisateur non trouvé")
    })
    public UserResponse updateUser(
            @Parameter(description = "Email de l'utilisateur à modifier", required = true, example = "john@example.com")
            @PathVariable String email,
            @RequestBody @Valid UserRequest request) {
        User userToUpdate = new User();
        userToUpdate.setEmail(email);
        userToUpdate.setPassword(request.password());
        userToUpdate.setRole(Role.User);
        User updated = userService.updateUser(userToUpdate);
        return toResponse(updated);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('Admin')")
    @Operation(
            summary = "Supprimer un utilisateur",
            description = "Réservé aux **Admins** uniquement."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé — rôle Admin requis"),
            @ApiResponse(responseCode = "400", description = "Utilisateur non trouvé")
    })
    public void deleteUser(
            @Parameter(description = "Email de l'utilisateur à supprimer", required = true)
            @PathVariable String email) {
        User userToDelete = new User();
        userToDelete.setEmail(email);
        userService.deleteUser(userToDelete);
    }

    @GetMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('Admin') or authentication.name == #email")
    @Operation(
            summary = "Récupérer un utilisateur",
            description = "Accessible par l'utilisateur lui-même ou un Admin."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé",
                    content = @Content(schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "email": "john@example.com",
                                      "role": "User",
                                      "emailVerified": true,
                                      "createdAt": "2026-03-15T10:30:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
            @ApiResponse(responseCode = "400", description = "Utilisateur non trouvé")
    })
    public UserResponse getUser(
            @Parameter(description = "Email de l'utilisateur", required = true, example = "john@example.com")
            @PathVariable String email) {
        User user = userService.getUserByEmail(email);
        return toResponse(user);
    }

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