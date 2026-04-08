package com.smartbilling.smartbilling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbilling.smartbilling.auth.service.EmailService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    // ← AJOUT : ObjectMapper non enregistré automatiquement en Spring Boot 4
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules(); // supporte LocalDateTime, etc.
    }

    // Mock JavaMailSender — pas d'envoi réel pendant les tests
    @Bean
    @Primary
    public JavaMailSender javaMailSender() {
        return mock(JavaMailSender.class);
    }

    // Mock EmailService — remplace l'implémentation Gmail
    @Bean
    @Primary
    public EmailService emailService() {
        return mock(EmailService.class);
    }
}