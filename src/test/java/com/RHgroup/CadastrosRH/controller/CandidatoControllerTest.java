package com.RHgroup.CadastrosRH.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhgroup.cadastrosrh.CadastrosRhApplication;
import com.rhgroup.cadastrosrh.dto.*;
import com.rhgroup.cadastrosrh.exception.ConflitoUnicidadeException;
import com.rhgroup.cadastrosrh.exception.GlobalExceptionHandler;
import com.rhgroup.cadastrosrh.exception.NotFoundException;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import com.rhgroup.cadastrosrh.service.CandidatoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment; // Para o webEnvironment
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//  Usamos @SpringBootTest para podermos declarar a classe
// principal e carregar o contexto web (MOCK) necessário.
@SpringBootTest(
        classes = CadastrosRhApplication.class,
        webEnvironment = WebEnvironment.MOCK
)
@AutoConfigureMockMvc(addFilters = false)
// O GlobalExceptionHandler é importante para testar retornos de erro
@Import(GlobalExceptionHandler.class)
@DisplayName("Web | CandidatoController")
class CandidatoControllerTest {

    private static final String BASE_URL = "/api/v1/candidatos";

    // O MockMvc é injetado graças ao @AutoConfigureMockMvc
    @Autowired private MockMvc mockMvc;
    // O ObjectMapper é resolvido com o contexto completo do @SpringBootTest
    @Autowired private ObjectMapper objectMapper;
    // Mockamos o Service para testar apenas o Controller
    @MockitoBean
    private CandidatoService candidatoService;

    private CandidatoCreateDTO novoCandidato() {
        CandidatoCreateDTO dto = new CandidatoCreateDTO();
        dto.setNome("Ana Viana");
        dto.setCpf("39053344705");
        dto.setEmail("ana@teste.com");
        dto.setSenha("minhasenhaforte");
        dto.setExperienciaAnos(2);
        dto.setStatus(StatusCandidato.CANDIDATO);
        return dto;
    }

    private CandidatoResponseDTO resposta(UUID id) {
        CandidatoResponseDTO dto = new CandidatoResponseDTO();
        dto.setId(id);
        dto.setNome("Ana Viana");
        dto.setEmail("ana@teste.com");
        dto.setStatus(StatusCandidato.CANDIDATO);
        dto.setCpf(null);
        return dto;
    }

    private CandidatoUpdateDTO dtoAtualizacao() {
        CandidatoUpdateDTO dto = new CandidatoUpdateDTO();
        dto.setNome("Ana Viana Atualizada");
        dto.setAreaInteresse("Fullstack");
        dto.setStatus(StatusCandidato.TRIAGEM);
        return dto;
    }

    private CandidatoPatchDTO dtoPatch() {
        CandidatoPatchDTO dto = new CandidatoPatchDTO();
        dto.setNome("Nome Parcial");
        dto.setPretensaoSalarial(new BigDecimal("4000.00"));
        return dto;
    }

    @Nested
    @DisplayName("POST /candidatos")
    class PostCandidato {

        @Test
        @DisplayName("201 | cria candidato válido")
        void deveCriar_201() throws Exception {
            UUID id = UUID.randomUUID();
            given(candidatoService.criar(any(CandidatoCreateDTO.class))).willReturn(resposta(id));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novoCandidato())))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString(BASE_URL + "/" + id)))
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.nome").value("Ana Viana"))
                    .andExpect(jsonPath("$.cpf").value(nullValue()));
        }

        @Test
        @DisplayName("409 | conflito de unicidade")
        void deveRetornarConflito_409() throws Exception {
            given(candidatoService.criar(any(CandidatoCreateDTO.class)))
                    .willThrow(new ConflitoUnicidadeException("CPF já cadastrado"));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(novoCandidato())))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /candidatos")
    class GetCandidato {

        @Test
        @DisplayName("200 | listar todos")
        void deveListarTodos_200() throws Exception {
            given(candidatoService.listarTodos()).willReturn(List.of(resposta(UUID.randomUUID())));

            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("200 | buscar por ID existente")
        void deveBuscarPorId_200() throws Exception {
            UUID id = UUID.randomUUID();
            given(candidatoService.buscarPorId(id)).willReturn(resposta(id));

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.cpf").value(nullValue()));
        }

        @Test
        @DisplayName("404 | buscar por ID inexistente")
        void deveRetornarNotFound_404() throws Exception {
            UUID id = UUID.randomUUID();
            given(candidatoService.buscarPorId(id))
                    .willThrow(new NotFoundException("Candidato não encontrado"));

            mockMvc.perform(get(BASE_URL + "/" + id))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("200 | buscar por status")
        void deveBuscarPorStatus_200() throws Exception {
            given(candidatoService.buscarPorStatus(StatusCandidato.CANDIDATO))
                    .willReturn(List.of(resposta(UUID.randomUUID())));

            mockMvc.perform(get(BASE_URL + "/status/" + StatusCandidato.CANDIDATO))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].status").value("CANDIDATO"));
        }
    }

    @Nested
    @DisplayName("PUT /candidatos/{id}")
    class PutCandidato {

        @Test
        @DisplayName("200 | atualizar candidato completo")
        void deveAtualizarCompleto_200() throws Exception {
            UUID id = UUID.randomUUID();
            CandidatoUpdateDTO updateDto = dtoAtualizacao();

            CandidatoResponseDTO respostaAtualizada = resposta(id);
            respostaAtualizada.setNome(updateDto.getNome());
            respostaAtualizada.setStatus(updateDto.getStatus());

            given(candidatoService.atualizar(eq(id), any(CandidatoUpdateDTO.class)))
                    .willReturn(respostaAtualizada);

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Ana Viana Atualizada"))
                    .andExpect(jsonPath("$.status").value("TRIAGEM"));
        }

        @Test
        @DisplayName("404 | ao atualizar ID inexistente")
        void deveRetornarNotFoundAoAtualizar_404() throws Exception {
            UUID id = UUID.randomUUID();
            given(candidatoService.atualizar(eq(id), any(CandidatoUpdateDTO.class)))
                    .willThrow(new NotFoundException("Candidato não encontrado"));

            mockMvc.perform(put(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dtoAtualizacao())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /candidatos/{id}")
    class PatchCandidato {

        @Test
        @DisplayName("200 | atualização parcial de dados")
        void deveAtualizarParcial_200() throws Exception {
            UUID id = UUID.randomUUID();
            CandidatoPatchDTO patchDto = dtoPatch();

            CandidatoResponseDTO respostaAtualizada = resposta(id);
            respostaAtualizada.setNome(patchDto.getNome());

            given(candidatoService.atualizarParcial(eq(id), any(CandidatoPatchDTO.class)))
                    .willReturn(respostaAtualizada);

            mockMvc.perform(patch(BASE_URL + "/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(patchDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Nome Parcial"));
        }

        @Test
        @DisplayName("204 | atualizar senha com sucesso")
        void deveAtualizarSenha_204() throws Exception {
            UUID id = UUID.randomUUID();
            CandidatoSenhaDTO dto = new CandidatoSenhaDTO();
            dto.setSenhaAntiga("antiga");
            dto.setSenhaNova("nova");

            doNothing().when(candidatoService).atualizarSenha(eq(id), any(CandidatoSenhaDTO.class));

            mockMvc.perform(patch(BASE_URL + "/" + id + "/senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("404 | ao tentar atualizar senha de ID inexistente")
        void deveRetornarNotFoundAoAtualizarSenha_404() throws Exception {
            UUID id = UUID.randomUUID();

            doThrow(new NotFoundException("Candidato não encontrado"))
                    .when(candidatoService).atualizarSenha(eq(id), any(CandidatoSenhaDTO.class));

            mockMvc.perform(patch(BASE_URL + "/" + id + "/senha")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"senhaAntiga\":\"a\",\"senhaNova\":\"b\"}"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /candidatos/{id}")
    class DeleteById {

        @Test
        @DisplayName("204 | exclui com sucesso")
        void deveDeletar_204() throws Exception {
            UUID id = UUID.randomUUID();
            doNothing().when(candidatoService).deletar(id);

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("404 | ao deletar ID inexistente")
        void deveRetornarNotFoundAoDeletar_404() throws Exception {
            UUID id = UUID.randomUUID();
            doThrow(new NotFoundException("Candidato não encontrado"))
                    .when(candidatoService).deletar(id);

            mockMvc.perform(delete(BASE_URL + "/" + id))
                    .andExpect(status().isNotFound());
        }
    }
}