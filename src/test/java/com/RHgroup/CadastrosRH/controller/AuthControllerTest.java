package com.RHgroup.CadastrosRH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhgroup.cadastrosrh.CadastrosRhApplication;
import com.rhgroup.cadastrosrh.repository.CandidatoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(
        classes = CadastrosRhApplication.class, // Declara a classe principal
        webEnvironment = WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@ActiveProfiles("h2")
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CandidatoRepository repository;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("POST /api/v1/auth/register | deve cadastrar novo usuário")
    void deveCadastrarUsuario() throws Exception {
        when(repository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("senhaHash");

        String body = """
                {
                  "nome": "Ana Viana",
                  "email": "ana@example.com",
                  "senha": "123456"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login | deve autenticar com sucesso")
    void deveAutenticar() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mockito.mock(UsernamePasswordAuthenticationToken.class));

        String body = """
                {
                  "email": "ana@example.com",
                  "senha": "123456"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }
}