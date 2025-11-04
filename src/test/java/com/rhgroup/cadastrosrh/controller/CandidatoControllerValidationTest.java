package com.rhgroup.cadastrosrh.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CandidatoControllerValidationTest {

    private final MockMvc mockMvc;

    CandidatoControllerValidationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveRetornar400ComMensagensQuandoPayloadInvalido() throws Exception {
        String payload = """
            {
              "cpf": "12345678901",
              "experienciaAnos": 2,
              "status": "CANDIDATO"
            }
        """;

        mockMvc.perform(
                        post("/api/v1/candidatos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.errors").exists());
    }
}
