package com.smartbilling.smartbilling.auth.controller;

import com.smartbilling.smartbilling.auth.dto.requests.LoginRequest;
import com.smartbilling.smartbilling.auth.dto.requests.UserRequest;
import com.smartbilling.smartbilling.auth.dto.responses.AuthResponse;
import com.smartbilling.smartbilling.auth.dto.responses.ErrorResponse;
import com.smartbilling.smartbilling.auth.dto.responses.MessageResponse;
import com.smartbilling.smartbilling.auth.service.AuthService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription, connexion et vérification email")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Créer un compte",
            description = "Crée un nouveau compte utilisateur et envoie un email de vérification. " +
                    "Le compte est utilisable immédiatement mais `emailVerified` sera `false`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compte créé avec succès",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = """
                                    {"message": "Inscription réussie. Vérifiez votre email pour activer votre compte."}
                                    """))),
            @ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public MessageResponse register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email et mot de passe (min. 6 caractères)",
                    content = @Content(examples = @ExampleObject(value = """
                            {"email": "john@example.com", "password": "secret123"}
                            """)))
            @RequestBody @Valid UserRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Se connecter",
            description = "Authentifie l'utilisateur et retourne un JWT. " +
                    "Si `emailVerified` est `false`, affichez un avertissement côté frontend."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie — JWT retourné",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                                      "tokenType": "Bearer",
                                      "role": "User",
                                      "emailVerified": false,
                                      "message": "Connexion réussie. Pensez à vérifier votre adresse email."
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AuthResponse login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                            {"email": "john@example.com", "password": "secret123"}
                            """)))
            @RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/verify-email")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Vérifier l'email",
            description = "Valide le token de vérification reçu par email. Le token expire après 24h."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email vérifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Token invalide ou expiré",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public MessageResponse verifyEmail(
            @Parameter(description = "Token UUID reçu par email", required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam String token) {
        return authService.verifyEmail(token);
    }

    @PostMapping("/resend-verification")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Renvoyer l'email de vérification",
            description = "Génère un nouveau token et renvoie l'email de vérification."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email renvoyé"),
            @ApiResponse(responseCode = "400", description = "Email déjà vérifié ou inconnu")
    })
    public MessageResponse resendVerification(
            @Parameter(description = "Email du compte", required = true, example = "john@example.com")
            @RequestParam String email) {
        return authService.resendVerificationEmail(email);
    }
}