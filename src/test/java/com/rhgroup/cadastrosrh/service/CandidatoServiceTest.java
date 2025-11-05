package com.rhgroup.cadastrosrh.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhgroup.cadastrosrh.dto.CandidatoCreateDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoResponseDTO;
import com.rhgroup.cadastrosrh.exception.ConflitoUnicidadeException;
import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import com.rhgroup.cadastrosrh.repository.CandidatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidatoServiceTest {

    @Mock CandidatoRepository repository;
    @Mock ObjectMapper objectMapper;
    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks
    CandidatoService service;

    @Test
    void criar_deveHashSenhaEValidarUnicidade_ok() {
        var dtoIn = CandidatoCreateDTO.builder()
                .nome("Ana Teste")
                .cpf("12345678901")
                .email("ana@x.com")
                .senha("segredo")
                .experienciaAnos(1)
                .status(StatusCandidato.CANDIDATO)
                .build();

        var candidatoSimulado = new Candidato();
        candidatoSimulado.setCpf("12345678901");

        when(repository.existsByCpf("12345678901")).thenReturn(false);
        when(repository.existsByEmail("ana@x.com")).thenReturn(false);
        when(passwordEncoder.encode("segredo")).thenReturn("$2a$10$hashsimulada");

        when(repository.save(any(Candidato.class))).thenReturn(candidatoSimulado);

        CandidatoResponseDTO salvo = service.criar(dtoIn);

        verify(passwordEncoder).encode("segredo");
        assertNotNull(salvo);
        verify(repository).save(argThat(c -> c.getSenha().startsWith("$2a$")));
    }

    @Test
    void criar_deveLancarConflitoQuandoCpfDuplicado() {
        var dtoIn = CandidatoCreateDTO.builder()
                .cpf("12345678901")
                .email("ana@x.com")
                .senha("123456")
                .experienciaAnos(0)
                .status(StatusCandidato.CANDIDATO)
                .build();

        when(repository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(ConflitoUnicidadeException.class, () -> service.criar(dtoIn));
        verify(repository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString()); // Não deve tentar criptografar
    }

    @Test
    void criar_deveLancarConflitoQuandoEmailDuplicado() {
        var dtoIn = CandidatoCreateDTO.builder()
                .cpf("12345678901")
                .email("ana@x.com")
                .senha("123456")
                .experienciaAnos(0)
                .status(StatusCandidato.CANDIDATO)
                .build();

        when(repository.existsByCpf("12345678901")).thenReturn(false);
        when(repository.existsByEmail("ana@x.com")).thenReturn(true);

        assertThrows(ConflitoUnicidadeException.class, () -> service.criar(dtoIn));
        verify(repository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }
}