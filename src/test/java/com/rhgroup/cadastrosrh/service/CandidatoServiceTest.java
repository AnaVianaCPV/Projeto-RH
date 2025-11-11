package com.rhgroup.cadastrosrh.service;

import com.rhgroup.cadastrosrh.dto.CandidatoCreateDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoPatchDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoSenhaDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoUpdateDTO;
import com.rhgroup.cadastrosrh.exception.NotFoundException;
import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import com.rhgroup.cadastrosrh.repository.CandidatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service | CandidatoService")
class CandidatoServiceTest {

    @Mock
    private CandidatoRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CandidatoService service;

    private UUID id;
    private Candidato candidato;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        candidato = Candidato.builder()
                .id(id)
                .nome("Ana Viana")
                .cpf("39053344705")
                .email("ana@teste.com")
                .senhaHash("{bcrypt}senha123")
                .status(StatusCandidato.CANDIDATO)
                .build();
    }

    private CandidatoCreateDTO buildCreateDTO() {
        CandidatoCreateDTO dto = new CandidatoCreateDTO();
        dto.setNome("Novo Teste");
        dto.setSenha("nova_senha");
        return dto;
    }

    private CandidatoUpdateDTO buildUpdateDTO() {
        CandidatoUpdateDTO dto = new CandidatoUpdateDTO();
        dto.setNome("Nome Atualizado");
        dto.setAreaInteresse("Fullstack");
        return dto;
    }

    private CandidatoPatchDTO buildPatchDTO() {
        CandidatoPatchDTO dto = new CandidatoPatchDTO();
        dto.setNome("Nome Parcial");
        dto.setStatus(StatusCandidato.APROVADO);
        return dto;
    }

    private CandidatoSenhaDTO buildSenhaDTO(String antiga, String nova) {
        CandidatoSenhaDTO dto = new CandidatoSenhaDTO();
        dto.setSenhaAntiga(antiga);
        dto.setSenhaNova(nova);
        return dto;
    }

    @Test
    @DisplayName("Deve criar um candidato com sucesso")
    void criar_sucesso() {
        CandidatoCreateDTO createDTO = buildCreateDTO();
        when(passwordEncoder.encode(createDTO.getSenha())).thenReturn("senha_hashed");
        when(repository.save(any(Candidato.class))).thenReturn(candidato);

        assertDoesNotThrow(() -> service.criar(createDTO));

        verify(passwordEncoder).encode("nova_senha");
        verify(repository).save(any(Candidato.class));
    }

    @Test
    @DisplayName("Deve listar todos os candidatos")
    void listarTodos_sucesso() {
        when(repository.findAll()).thenReturn(List.of(candidato));

        List<?> resultado = service.listarTodos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Deve buscar candidato por ID e retornar DTO")
    void buscarPorId_sucesso() {
        when(repository.findById(id)).thenReturn(Optional.of(candidato));

        assertDoesNotThrow(() -> service.buscarPorId(id));

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao buscar ID inexistente")
    void buscarPorId_notFound() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.buscarPorId(id));

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar todos os campos (PUT) com sucesso")
    void atualizar_sucesso() {
        CandidatoUpdateDTO updateDTO = buildUpdateDTO();
        when(repository.findById(id)).thenReturn(Optional.of(candidato));
        when(repository.save(any(Candidato.class))).thenReturn(candidato);

        service.atualizar(id, updateDTO);

        assertEquals("Nome Atualizado", candidato.getNome());
        assertEquals("Fullstack", candidato.getAreaInteresse());
        verify(repository).findById(id);
        verify(repository).save(candidato);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao atualizar ID inexistente")
    void atualizar_notFound() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.atualizar(id, buildUpdateDTO()));

        verify(repository).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar parcialmente campos (PATCH) com sucesso")
    void atualizarParcial_sucesso() {
        CandidatoPatchDTO patchDTO = buildPatchDTO();
        when(repository.findById(id)).thenReturn(Optional.of(candidato));
        when(repository.save(any(Candidato.class))).thenReturn(candidato);

        service.atualizarParcial(id, patchDTO);

        assertEquals("Nome Parcial", candidato.getNome());
        assertEquals(StatusCandidato.APROVADO, candidato.getStatus());
        verify(repository).save(candidato);
    }

    @Test
    @DisplayName("Deve atualizar a senha com sucesso")
    void atualizarSenha_sucesso() {
        CandidatoSenhaDTO dto = buildSenhaDTO("senha_antiga", "nova_senha_forte");
        when(repository.findById(id)).thenReturn(Optional.of(candidato));
        when(passwordEncoder.matches("senha_antiga", candidato.getSenhaHash())).thenReturn(true);
        when(passwordEncoder.encode("nova_senha_forte")).thenReturn("nova_hash");

        assertDoesNotThrow(() -> service.atualizarSenha(id, dto));

        verify(passwordEncoder).encode("nova_senha_forte");
        assertEquals("nova_hash", candidato.getSenhaHash());
    }

    @Test
    @DisplayName("Deve falhar ao atualizar senha com senha antiga incorreta")
    void atualizarSenha_falhaSenhaIncorreta() {
        CandidatoSenhaDTO dto = buildSenhaDTO("senha_errada", "nova_senha_forte");
        when(repository.findById(id)).thenReturn(Optional.of(candidato));
        when(passwordEncoder.matches("senha_errada", candidato.getSenhaHash())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.atualizarSenha(id, dto));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar candidato com sucesso")
    void deletar_sucesso() {
        when(repository.findById(id)).thenReturn(Optional.of(candidato));
        doNothing().when(repository).delete(candidato);

        assertDoesNotThrow(() -> service.deletar(id));

        verify(repository).findById(id);
        verify(repository).delete(candidato);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao deletar ID inexistente")
    void deletar_notFound() {
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.deletar(id));

        verify(repository).findById(id);
        verify(repository, never()).delete(any());
    }
}