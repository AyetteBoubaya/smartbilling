package com.smartbilling.smartbilling.auth.controller;

import com.smartbilling.smartbilling.auth.dto.requests.ForgotPasswordRequest;
import com.smartbilling.smartbilling.auth.dto.requests.ResetPasswordRequest;
import com.smartbilling.smartbilling.auth.dto.responses.ErrorResponse;
import com.smartbilling.smartbilling.auth.dto.responses.MessageResponse;
import com.smartbilling.smartbilling.auth.service.PasswordService;
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
@Tag(name = "Mot de passe", description = "Réinitialisation du mot de passe via lien ou OTP")
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Demander une réinitialisation",
            description = """
                    Envoie simultanément :
                    - Un **lien** avec token UUID (valable 15 min) pour les clients web
                    - Un **code OTP** à 6 chiffres (valable 15 min) pour les clients mobile
                    
                    **Sécurité** : la réponse est identique que l'email existe ou non (anti-énumération).
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email envoyé (ou email inconnu — même réponse)",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class),
                            examples = @ExampleObject(value = """
                                    {"message": "Si cet email existe, vous recevrez un lien et un code de réinitialisation."}
                                    """)))
    })
    public MessageResponse forgotPassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                            {"email": "john@example.com"}
                            """)))
            @RequestBody @Valid ForgotPasswordRequest request) {
        return passwordService.forgotPassword(request);
    }

    @PostMapping("/reset-password/link")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Réinitialiser via lien (token UUID)",
            description = "Utilise le token UUID reçu dans le lien email. " +
                    "Après succès, tous les tokens actifs du user sont invalidés."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé"),
            @ApiResponse(responseCode = "400", description = "Token invalide/expiré ou mots de passe différents",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public MessageResponse resetByLink(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "token": "550e8400-e29b-41d4-a716-446655440000",
                              "newPassword": "nouveauSecret123",
                              "confirmPassword": "nouveauSecret123"
                            }
                            """)))
            @RequestBody @Valid ResetPasswordRequest request) {
        return passwordService.resetPasswordByLink(request);
    }

    @PostMapping("/reset-password/otp")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Réinitialiser via OTP (code 6 chiffres)",
            description = "Utilise le code OTP à 6 chiffres reçu par email. " +
                    "Après succès, tous les tokens actifs du user sont invalidés."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mot de passe réinitialisé"),
            @ApiResponse(responseCode = "400", description = "OTP invalide/expiré ou mots de passe différents",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public MessageResponse resetByOtp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "token": "847291",
                              "newPassword": "nouveauSecret123",
                              "confirmPassword": "nouveauSecret123"
                            }
                            """)))
            @RequestBody @Valid ResetPasswordRequest request) {
        return passwordService.resetPasswordByOtp(request);
    }
}