package com.rhgroup.cadastrosrh.security;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Security | CandidatoController")
class CandidatoSecurityTest {

    private static final String BASE_URL = "/api/v1/candidatos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CandidatoService candidatoService;

    private Candidato buildCandidatoEntidade(UUID id) {
        return Candidato.builder()
                .id(id)
                .nome("Teste Segurança")
                .cpf("11122233344")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .email("teste@seguranca.com")
                .senhaHash("senha_codificada")
                .celular("1199887766")
                .areaInteresse("Backend")
                .experienciaAnos(5)
                .pretensaoSalarial(new BigDecimal("6000.00"))
                .status(StatusCandidato.CANDIDATO)
                .build();
    }

    private CandidatoResponseDTO buildResponseDTO(UUID id) {
        Candidato candidato = buildCandidatoEntidade(id);
        return CandidatoResponseDTO.fromEntity(candidato);
    }

    private CandidatoCreateDTO buildCreateDTO() {
        var dto = new CandidatoCreateDTO();
        dto.setNome("Novo Candidato");
        dto.setCpf("39053344705");
        dto.setDataNascimento(LocalDate.of(2000, 1, 1));
        dto.setEmail("anonimo@teste.com");
        dto.setSenha("minhasenhaforte");
        dto.setCelular("11999999999");
        dto.setAreaInteresse("Backend");
        dto.setExperienciaAnos(2);
        dto.setPretensaoSalarial(new BigDecimal("3500.00"));
        dto.setStatus(StatusCandidato.CANDIDATO);
        return dto;
    }

    @Nested
    @DisplayName("Rotas públicas")
    class PublicRoutes {

        @Test
        @DisplayName("POST / | anônimo deve criar (201)")
        @WithAnonymousUser
        void post_anonimo_deveCriar_201() throws Exception {
            Mockito.when(candidatoService.criar(any(CandidatoCreateDTO.class)))
                    .thenReturn(buildResponseDTO(UUID.randomUUID()));

            String body = objectMapper.writeValueAsString(buildCreateDTO());

            mockMvc.perform(post(BASE_URL)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("Rotas protegidas (CRUD completo)")
    class ProtectedRoutes {


        @Test
        @DisplayName("GET /{id} | anônimo deve retornar 401")
        @WithAnonymousUser
        void get_porId_anonimo_401() throws Exception {
            mockMvc.perform(get(BASE_URL + "/" + UUID.randomUUID()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /{id} | autenticado deve retornar 200")
        @WithMockUser(username = "teste@seguranca.com")
        void get_porId_autenticado_200() throws Exception {
            UUID id = UUID.randomUUID();
            Candidato candidato = buildCandidatoEntidade(id);

            Mockito.when(candidatoService.buscarPorId(id)).thenReturn(CandidatoResponseDTO.fromEntity(candidato));

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isOk());
        }


        @Test
        @DisplayName("GET / | anônimo deve retornar 401")
        @WithAnonymousUser
        void get_anonimo_401() throws Exception {
            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET / | autenticado deve retornar 200")
        @WithMockUser(username = "teste@seguranca.com")
        void get_autenticado_200() throws Exception {
            Mockito.when(candidatoService.listarTodos()).thenReturn(Collections.emptyList());

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk());
        }


        @Test
        @DisplayName("GET /status/{status} | anônimo deve retornar 401")
        @WithAnonymousUser
        void get_porStatus_anonimo_401() throws Exception {
            mockMvc.perform(get(BASE_URL + "/status/" + StatusCandidato.CANDIDATO))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("GET /status/{status} | autenticado deve retornar 200")
        @WithMockUser(username = "teste@seguranca.com")
        void get_porStatus_autenticado_200() throws Exception {
            Mockito.when(candidatoService.buscarPorStatus(StatusCandidato.CANDIDATO)).thenReturn(List.of(buildResponseDTO(UUID.randomUUID())));

            mockMvc.perform(get(BASE_URL + "/status/" + StatusCandidato.CANDIDATO))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("PUT /{id} | anônimo deve retornar 401")
        @WithAnonymousUser
        void put_anonimo_401() throws Exception {
            String body = objectMapper.writeValueAsString(buildCreateDTO());
            mockMvc.perform(put(BASE_URL + "/" + UUID.randomUUID())
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PUT /{id} | autenticado deve retornar 200")
        @WithMockUser(username = "teste@seguranca.com")
        void put_autenticado_200() throws Exception {
            UUID id = UUID.randomUUID();
            Mockito.when(candidatoService.atualizar(any(UUID.class), any())).thenReturn(buildResponseDTO(id));

            String body = objectMapper.writeValueAsString(buildCreateDTO());

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk());
        }


        @Test
        @DisplayName("DELETE /{id} | anônimo deve retornar 401")
        @WithAnonymousUser
        void delete_anonimo_401() throws Exception {
            mockMvc.perform(delete(BASE_URL + "/" + UUID.randomUUID()).with(csrf()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("DELETE /{id} | autenticado deve retornar 204")
        @WithMockUser(username = "teste@seguranca.com")
        void delete_autenticado_204() throws Exception {
            UUID id = UUID.randomUUID();
            doNothing().when(candidatoService).deletar(id);

            mockMvc.perform(delete(BASE_URL + "/" + id).with(csrf()))
                    .andExpect(status().isNoContent());
        }


        @Test
        @DisplayName("PATCH /{id}/senha | anônimo deve retornar 401")
        @WithAnonymousUser
        void patch_senha_anonimo_401() throws Exception {
            String body = "{\"senhaAntiga\": \"123\", \"senhaNova\": \"456\"}";
            mockMvc.perform(patch(BASE_URL + "/" + UUID.randomUUID() + "/senha")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("PATCH /{id}/senha | autenticado deve retornar 204")
        @WithMockUser(username = "teste@seguranca.com")
        void patch_senha_autenticado_204() throws Exception {
            UUID id = UUID.randomUUID();
            String body = "{\"senhaAntiga\": \"123\", \"senhaNova\": \"456\"}";
            doNothing().when(candidatoService).atualizarSenha(any(UUID.class), any());

            mockMvc.perform(patch(BASE_URL + "/" + id + "/senha")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isNoContent());
        }
    }
}