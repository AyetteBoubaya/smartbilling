package com.smartbilling.smartbilling.auth.controller;

import com.smartbilling.smartbilling.auth.dto.requests.LoginRequest;
import com.smartbilling.smartbilling.auth.dto.requests.RefreshTokenRequest;
import com.smartbilling.smartbilling.auth.dto.requests.UserRequest;
import com.smartbilling.smartbilling.auth.dto.responses.AuthResponse;
import com.smartbilling.smartbilling.auth.dto.responses.ErrorResponse;
import com.smartbilling.smartbilling.auth.dto.responses.MessageResponse;
import com.smartbilling.smartbilling.auth.dto.responses.RefreshTokenResponse;
import com.smartbilling.smartbilling.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription, connexion, refresh token et déconnexion")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un compte",
            description = "Crée un compte et envoie un email de vérification.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compte créé",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Email déjà utilisé",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public MessageResponse register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "\"\"{\"email\": \"john@example.com\", \"password\": \"secret123\"}")))
            @RequestBody @Valid UserRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Se connecter",
            description = "Retourne un `accessToken` (15min) et un `refreshToken` (7 jours).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "accessToken": "eyJhbGci...",
                                      "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
                                      "tokenType": "Bearer",
                                      "expiresIn": 900,
                                      "role": "User",
                                      "emailVerified": true,
                                      "message": "Connexion réussie."
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "Identifiants incorrects",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AuthResponse login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "\"\"{\"email\": \"john@example.com\", \"password\": \"secret123\"}\"\"")))
            @RequestBody @Valid LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Rafraîchir le token",
            description = """
                    Échange un `refreshToken` valide contre un nouveau couple `accessToken` + `refreshToken`.
                    
                    **Rotation** : l'ancien `refreshToken` est immédiatement invalidé.
                    
                    **Détection de vol** : si un `refreshToken` déjà utilisé est présenté,
                    tous les tokens du user sont révoqués et il doit se reconnecter.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Nouveaux tokens générés",
                    content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "accessToken": "eyJhbGci...",
                                      "refreshToken": "nouveau-uuid-ici",
                                      "tokenType": "Bearer",
                                      "expiresIn": 900
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Refresh token invalide, expiré ou révoqué",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public RefreshTokenResponse refresh(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "{\"refreshToken\": \"550e8400-e29b-41d4-a716-446655440000\"}")))
            @RequestBody @Valid RefreshTokenRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Se déconnecter",
            description = "Révoque le `refreshToken` et tous les tokens actifs du user. " +
                    "Le client doit supprimer les tokens côté frontend.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Refresh token invalide")
    })
    public MessageResponse logout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(
                            value = "\"\"{\"refreshToken\": \"550e8400-e29b-41d4-a716-446655440000\"}\"\"")))
            @RequestBody @Valid RefreshTokenRequest request) {
        return authService.logout(request);
    }

    @GetMapping("/verify-email")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Vérifier l'email", description = "Valide le token reçu par email (expire 24h).")
    public MessageResponse verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
    }

    @PostMapping("/resend-verification")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Renvoyer l'email de vérification")
    public MessageResponse resendVerification(@RequestParam String email) {
        return authService.resendVerificationEmail(email);
    }
}