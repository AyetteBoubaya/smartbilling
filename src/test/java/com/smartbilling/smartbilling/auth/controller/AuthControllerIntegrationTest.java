package com.smartbilling.smartbilling.auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbilling.smartbilling.TestConfig;
import com.smartbilling.smartbilling.auth.dto.requests.LoginRequest;
import com.smartbilling.smartbilling.auth.dto.requests.RefreshTokenRequest;
import com.smartbilling.smartbilling.auth.dto.requests.UserRequest;
import com.smartbilling.smartbilling.auth.repository.RefreshTokenRepository;
import com.smartbilling.smartbilling.auth.repository.TokenRepository;
import com.smartbilling.smartbilling.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
@DisplayName("AuthController — Tests d'intégration")
class AuthControllerIntegrationTest {

    @Autowired WebApplicationContext context;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired TokenRepository tokenRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Construction manuelle de MockMvc avec Spring Security
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        refreshTokenRepository.deleteAll();
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    // ══════════════════════════════════════════════════════════
    //  REGISTER
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /register — 201 : compte créé")
    void register_shouldReturn201() throws Exception {
        UserRequest request = new UserRequest("john@example.com", "secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value(containsString("Inscription réussie")));

        assertThat(userRepository.existsByEmail("john@example.com")).isTrue();
    }

    @Test
    @DisplayName("POST /register — 400 : email déjà utilisé")
    void register_duplicateEmail_shouldReturn400() throws Exception {
        UserRequest request = new UserRequest("john@example.com", "secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("déjà utilisé")));
    }

    @Test
    @DisplayName("POST /register — 400 : email invalide")
    void register_invalidEmail_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("not-an-email", "secret123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.email").exists());
    }

    @Test
    @DisplayName("POST /register — 400 : mot de passe trop court")
    void register_shortPassword_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("john@example.com", "abc"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.password").exists());
    }

    // ══════════════════════════════════════════════════════════
    //  LOGIN
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /login — 200 : retourne accessToken + refreshToken")
    void login_shouldReturn200WithTokens() throws Exception {
        registerUser("john@example.com", "secret123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("john@example.com", "secret123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andExpect(jsonPath("$.emailVerified").value(false));
    }

    @Test
    @DisplayName("POST /login — 401 : mauvais mot de passe")
    void login_wrongPassword_shouldReturn401() throws Exception {
        registerUser("john@example.com", "secret123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("john@example.com", "wrongpassword"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(containsString("incorrect")));
    }

    // ══════════════════════════════════════════════════════════
    //  REFRESH
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /refresh — 200 : rotation des tokens")
    void refresh_shouldReturn200WithNewTokens() throws Exception {
        registerUser("john@example.com", "secret123");
        String refreshToken = loginAndGetRefreshToken("john@example.com", "secret123");

        MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RefreshTokenRequest(refreshToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        String newRefreshToken = objectMapper.readTree(
                        result.getResponse().getContentAsString())
                .get("refreshToken").asText();
        assertThat(newRefreshToken).isNotEqualTo(refreshToken);
    }

    @Test
    @DisplayName("POST /refresh — 400 : réutilisation d'un token révoqué")
    void refresh_revokedToken_shouldReturn400() throws Exception {
        registerUser("john@example.com", "secret123");
        String refreshToken = loginAndGetRefreshToken("john@example.com", "secret123");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RefreshTokenRequest(refreshToken))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RefreshTokenRequest(refreshToken))))
                .andExpect(status().isBadRequest());
    }

    // ══════════════════════════════════════════════════════════
    //  LOGOUT
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /logout — 200 : déconnexion réussie")
    void logout_shouldReturn200() throws Exception {
        registerUser("john@example.com", "secret123");
        String refreshToken = loginAndGetRefreshToken("john@example.com", "secret123");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RefreshTokenRequest(refreshToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("Déconnexion")));
    }

    @Test
    @DisplayName("POST /logout — refresh token invalide après logout")
    void logout_refreshTokenInvalidatedAfterLogout() throws Exception {
        registerUser("john@example.com", "secret123");
        String refreshToken = loginAndGetRefreshToken("john@example.com", "secret123");

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RefreshTokenRequest(refreshToken))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RefreshTokenRequest(refreshToken))))
                .andExpect(status().isBadRequest());
    }

    // ══════════════════════════════════════════════════════════
    //  Helpers
    // ══════════════════════════════════════════════════════════

    private void registerUser(String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest(email, password))))
                .andExpect(status().isCreated());
    }

    private String loginAndGetRefreshToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest(email, password))))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("refreshToken").asText();
    }
}