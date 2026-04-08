package com.smartbilling.smartbilling.auth.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbilling.smartbilling.TestConfig;
import com.smartbilling.smartbilling.auth.domain.Role;
import com.smartbilling.smartbilling.auth.domain.User;
import com.smartbilling.smartbilling.auth.dto.requests.LoginRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
@DisplayName("UserController — Tests d'intégration")
class UserControllerIntegrationTest {

    @Autowired WebApplicationContext context;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired TokenRepository tokenRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        refreshTokenRepository.deleteAll();
        tokenRepository.deleteAll();
        userRepository.deleteAll();

        createUserDirectly("user@example.com", "secret123", Role.User);
        userToken = loginAndGetAccessToken("user@example.com", "secret123");

        createUserDirectly("admin@example.com", "secret123", Role.Admin);
        adminToken = loginAndGetAccessToken("admin@example.com", "secret123");
    }

    // ══════════════════════════════════════════════════════════
    //  GET
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /{email} — 200 : l'utilisateur accède à son propre profil")
    void getUser_ownProfile_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/users/user@example.com")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("User"));
    }

    @Test
    @DisplayName("GET /{email} — 200 : Admin accède au profil d'un autre user")
    void getUser_adminAccessOtherUser_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/users/user@example.com")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /{email} — 403 : user accède au profil d'un autre")
    void getUser_accessOtherUser_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/users/admin@example.com")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /{email} — 401 : sans token")
    void getUser_noToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/users/user@example.com"))
                .andExpect(status().isUnauthorized());
    }

    // ══════════════════════════════════════════════════════════
    //  PUT
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("PUT /{email} — 200 : l'utilisateur met à jour son propre profil")
    void updateUser_ownProfile_shouldReturn200() throws Exception {
        mockMvc.perform(put("/api/users/user@example.com")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("user@example.com", "newSecret123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    @DisplayName("PUT /{email} — 403 : user modifie le profil d'un autre")
    void updateUser_otherUser_shouldReturn403() throws Exception {
        mockMvc.perform(put("/api/users/admin@example.com")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserRequest("admin@example.com", "newSecret123"))))
                .andExpect(status().isForbidden());
    }

    // ══════════════════════════════════════════════════════════
    //  DELETE
    // ══════════════════════════════════════════════════════════

    @Test
    @DisplayName("DELETE /{email} — 204 : Admin supprime un utilisateur")
    void deleteUser_admin_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/users/user@example.com")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /{email} — 403 : User ne peut pas supprimer")
    void deleteUser_notAdmin_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/users/admin@example.com")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /{email} — 401 : sans token")
    void deleteUser_noToken_shouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/users/user@example.com"))
                .andExpect(status().isUnauthorized());
    }

    // ══════════════════════════════════════════════════════════
    //  Helpers
    // ══════════════════════════════════════════════════════════

    private void createUserDirectly(String email, String password, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEmailVerified(true);
        user.setEnabled(true);
        userRepository.save(user);
    }

    private String loginAndGetAccessToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest(email, password))))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
    }
}