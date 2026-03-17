package com.smartbilling.smartbilling.shared.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.frontend-url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI smartBillingOpenAPI() {
        return new OpenAPI()
                // ── Informations générales ─────────────────────────
                .info(new Info()
                        .title("SmartBilling API")
                        .description("""
                                API REST complète pour la gestion de facturation intelligente.
                                
                                ## Authentification
                                Cette API utilise **JWT Bearer Token**. Pour accéder aux endpoints protégés :
                                1. Créez un compte via `POST /api/auth/register`
                                2. Connectez-vous via `POST /api/auth/login`
                                3. Copiez le `accessToken` reçu
                                4. Cliquez sur **Authorize** en haut à droite et collez le token
                                
                                ## Vérification email
                                Après inscription, un email de vérification est envoyé.
                                La connexion est possible même sans vérification,
                                mais le champ `emailVerified` sera `false` dans la réponse.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SmartBilling Team")
                                .email("contact@smartbilling.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                )
                // ── Serveurs ───────────────────────────────────────
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Développement local"),
                        new Server().url(serverUrl).description("Serveur de production")
                ))
                // ── Schéma de sécurité JWT ─────────────────────────
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Collez votre JWT token ici (sans le préfixe 'Bearer')")
                        )
                );
    }
}
