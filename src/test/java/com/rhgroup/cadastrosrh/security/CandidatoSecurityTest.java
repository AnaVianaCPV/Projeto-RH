package com.rhgroup.cadastrosrh.security;

import com.rhgroup.cadastrosrh.controller.CandidatoController;
import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import com.rhgroup.cadastrosrh.service.CandidatoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito; // Importação do Mockito
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; // Nova importação
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidatoController.class)
@DisplayName("Testes de Segurança para CandidatoController")
class CandidatoSecurityTest {

    // CandidatoService agora será injetado via a classe de Configuração de Teste
    @Autowired
    private MockMvc mockMvc;


    // Construtor com 13 argumentos na ordem do AllArgsConstructor:
    private final Candidato mockCandidato = new Candidato(
            UUID.randomUUID(), // 1. id
            "Teste Seguranca",  // 2. nome
            "11122233344",      // 3. cpf
            LocalDate.of(1990, 1, 1), // 4. dataNascimento
            "teste@seguranca.com",    // 5. email
            "senha_codificada", // 6. senha
            "99887766",         // 7. celular
            "Backend",          // 8. areaInteresse
            5,                  // 9. experienciaAnos
            new BigDecimal("6000.00"), // 10. pretensaoSalarial
            StatusCandidato.ATIVO,     // 11. status
            null, // 12. criadoEm
            null  // 13. atualizadoEm
    );

    /**
     * Classe de Configuração Aninhada para fornecer o Mock do CandidatoService.
     * Esta é a alternativa recomendada ao @MockBean para evitar a depreciação.
     */
    @Configuration
    static class TestConfig {
        @Bean
        public CandidatoService candidatoService() {
            // Cria e expõe o mock do serviço como um bean do Spring
            return Mockito.mock(CandidatoService.class);
        }
    }

    // --- Teste de Acesso a Rota Pública (Cadastro) ---

    @Test
    @DisplayName("POST /api/candidatos deve permitir acesso a usuários anônimos")
    @WithAnonymousUser
    void postCandidato_DevePermitirAcessoAnonimo(@Autowired CandidatoService candidatoService) throws Exception {
        // Arrange
        // Usamos o mockService injetado como argumento no método de teste
        when(candidatoService.salvar(any(Candidato.class))).thenReturn(mockCandidato);

        String candidatoJson = "{\"nome\": \"Novo Candidato\", \"cpf\": \"99999999999\", \"email\": \"anonimo@teste.com\", \"senha\": \"minhasenhaforte\", \"experienciaAnos\": 2, \"status\": \"ATIVO\"}";

        // Act & Assert
        mockMvc.perform(post("/api/candidatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(candidatoJson))
                .andExpect(status().isCreated());
    }

    // --- Teste de Acesso a Rota Protegida (Busca por ID) ---

    @Test
    @DisplayName("GET /api/candidatos/{id} deve negar acesso a usuários anônimos (401)")
    @WithAnonymousUser
    void getCandidatoById_DeveNegarAcessoAnonimo() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/candidatos/" + mockCandidato.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/candidatos/{id} deve permitir acesso a usuários autenticados (200)")
    @WithMockUser(username = "teste@seguranca.com", roles = "USER")
    void getCandidatoById_DevePermitirAcessoAutenticado(@Autowired CandidatoService candidatoService) throws Exception {
        // Arrange
        // Usamos o mockService injetado como argumento no método de teste
        when(candidatoService.buscarPorId(any(UUID.class))).thenReturn(mockCandidato);

        // Act & Assert
        mockMvc.perform(get("/api/candidatos/" + mockCandidato.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}