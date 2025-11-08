package com.rhgroup.cadastrosrh.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhgroup.cadastrosrh.dto.CandidatoCreateDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoResponseDTO;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import com.rhgroup.cadastrosrh.service.CandidatoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CandidatoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CandidatoController | Validações e criação")
class CandidatoControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CandidatoService candidatoService;

    private String jsonValido() throws Exception {
        CandidatoCreateDTO dto = new CandidatoCreateDTO();
        dto.setNome("Ana Viana");
        dto.setCpf("39053344705"); // CPF com 11 dígitos (formato simples)
        dto.setEmail("ana@teste.com");
        dto.setSenha("segura123");
        dto.setCelular("11999999999");
        dto.setAreaInteresse("Backend");
        dto.setExperienciaAnos(2);
        dto.setPretensaoSalarial(new BigDecimal("3500.00"));
        dto.setStatus(StatusCandidato.ATIVO);
        return objectMapper.writeValueAsString(dto);
    }

    private String jsonInvalidoMinimo() {
        // faltando campos obrigatórios como nome/email/senha
        return """
        {
          "cpf": "12345678901",
          "experienciaAnos": 2,
          "status": "ATIVO"
        }
        """;
    }

    @Nested
    @DisplayName("POST /api/v1/candidatos")
    class PostCandidatos {

        @Test
        @DisplayName("Deve retornar 400 (ProblemDetail) quando payload é inválido")
         void deveRetornar400ComMensagensQuandoPayloadInvalido() throws Exception {
            mockMvc.perform(post("/api/v1/candidatos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(jsonInvalidoMinimo()))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                    .andExpect(jsonPath("$.title").value("Dados inválidos"))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.errors").isArray())
                    .andExpect(jsonPath("$.errors.length()", greaterThanOrEqualTo(1)))
                    .andExpect(jsonPath("$.errors[*].field", hasItem(anyOf(
                            equalTo("nome"), equalTo("email"), equalTo("senha")
                    ))))
                    .andExpect(jsonPath("$.errors[*].message", hasItem(containsString("obrigatório"))));
        }

        @Test
        @DisplayName("Deve retornar 201 (Created) com Location e corpo quando payload é válido")
        void deveCriarCandidato_201() throws Exception {
            UUID id = UUID.randomUUID();
            CandidatoResponseDTO resposta = new CandidatoResponseDTO();
            resposta.setId(id);
            resposta.setNome("Ana Viana");
            resposta.setEmail("ana@teste.com");
            resposta.setStatus(StatusCandidato.ATIVO);

            when(candidatoService.criar(any())).thenReturn(resposta);

            mockMvc.perform(post("/api/v1/candidatos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(jsonValido()))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", endsWith("/" + id)))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.nome").value("Ana Viana"))
                    .andExpect(jsonPath("$.email").value("ana@teste.com"))
                    .andExpect(jsonPath("$.status").value("ATIVO"));
        }

        @Test
        @DisplayName("Deve retornar 415 quando Content-Type não é application/json")
        void deveRetornar415QuandoContentTypeInvalido() throws Exception {
            mockMvc.perform(post("/api/v1/candidatos")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("nome=Ana"))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }
}
