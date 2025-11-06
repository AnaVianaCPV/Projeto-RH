package com.rhgroup.cadastrosrh.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhgroup.cadastrosrh.controller.CandidatoController;
import com.rhgroup.cadastrosrh.dto.CandidatoCreateDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoResponseDTO;
import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import com.rhgroup.cadastrosrh.service.CandidatoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = CandidatoController.class)
@DisplayName("Security | CandidatoController")
class CandidatoSecurityTest {

    private static final String BASE_URL = "/api/v1/candidatos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        CandidatoService candidatoService() {
            return Mockito.mock(CandidatoService.class);
        }
    }

    private CandidatoResponseDTO buildResponseDTO() {
        CandidatoResponseDTO dto = new CandidatoResponseDTO();
        dto.setId(UUID.randomUUID());
        dto.setNome("Novo Candidato");
        dto.setEmail("anonimo@teste.com");
        dto.setStatus(StatusCandidato.ATIVO);
        return dto;
    }

    private Candidato buildEntity(UUID id) {
        return new Candidato(
                id,
                "Teste Segurança",
                "11122233344",
                LocalDate.of(1990, 1, 1),
                "teste@seguranca.com",
                "senha_codificada",
                "1199887766",
                "Backend",
                5,
                new BigDecimal("6000.00"),
                StatusCandidato.ATIVO,
                null,
                null
        );
    }

    private CandidatoCreateDTO buildCreateDTO() {
        CandidatoCreateDTO dto = new CandidatoCreateDTO();
        dto.setNome("Novo Candidato");
        dto.setCpf("99999999999");
        dto.setEmail("anonimo@teste.com");
        dto.setSenha("minhasenhaforte");
        dto.setCelular("11999999999");
        dto.setAreaInteresse("Backend");
        dto.setExperienciaAnos(2);
        dto.setPretensaoSalarial(new BigDecimal("3500.00"));
        dto.setStatus(StatusCandidato.ATIVO);
        return dto;
    }

    @Nested
    @DisplayName("Rotas públicas")
    class PublicRoutes {

        @Test
        @DisplayName("POST /api/v1/candidatos | anônimo deve conseguir criar (201)")
        @WithAnonymousUser
        void post_anonimo_deveCriar_201(@Autowired CandidatoService candidatoService) throws Exception {
            when(candidatoService.criar(any(CandidatoCreateDTO.class)))
                    .thenReturn(buildResponseDTO());

            String body = objectMapper.writeValueAsString(buildCreateDTO());

            // Act & Assert
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("Rotas protegidas")
    class ProtectedRoutes {

        @Test
        @DisplayName("GET /api/v1/candidatos/{id} | anônimo deve receber 401")
        @WithAnonymousUser
        void get_porId_anonimo_401() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(get(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /api/v1/candidatos/{id} | autenticado deve receber 200")
        @WithMockUser(username = "teste@seguranca.com", roles = {"USER"})
        void get_porId_autenticado_200(@Autowired CandidatoService candidatoService) throws Exception {
            UUID id = UUID.randomUUID();
            when(candidatoService.buscarPorId(id)).thenReturn(buildEntity(id));

            mockMvc.perform(get(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }
}
