package com.rhgroup.cadastrosrh.repository;

import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Repository | CandidatoRepository")
class CandidatoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CandidatoRepository repository;

    private Candidato candidatoAtivo;
    private Candidato candidatoTriagem;

    @BeforeEach
    void setup() {
        // Limpa o banco in-memory antes de cada teste
        entityManager.clear();

        candidatoAtivo = Candidato.builder()
                .nome("Alice Teste")
                .cpf("11122233344")
                .email("alice@teste.com")
                .senhaHash("hash123")
                .status(StatusCandidato.CANDIDATO)
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .experienciaAnos(5)
                .pretensaoSalarial(new BigDecimal("5000.00"))
                .build();

        candidatoTriagem = Candidato.builder()
                .nome("Bob Teste")
                .cpf("44455566677")
                .email("bob@teste.com")
                .senhaHash("hash456")
                .status(StatusCandidato.TRIAGEM)
                .dataNascimento(LocalDate.of(1995, 5, 5))
                .experienciaAnos(2)
                .pretensaoSalarial(new BigDecimal("3000.00"))
                .build();

        // Persiste as entidades no banco de dados in-memory
        entityManager.persist(candidatoAtivo);
        entityManager.persist(candidatoTriagem);
        entityManager.flush();
    }

    // --- Testes de Existência e Unicidade ---

    @Test
    @DisplayName("Deve retornar true se o CPF existir")
    void existsByCpf_retornaTrue() {
        boolean existe = repository.existsByCpf("11122233344");
        assertTrue(existe);
    }

    @Test
    @DisplayName("Deve retornar false se o CPF não existir")
    void existsByCpf_retornaFalse() {
        boolean existe = repository.existsByCpf("99988877766");
        assertFalse(existe);
    }

    @Test
    @DisplayName("Deve retornar true se o Email existir")
    void existsByEmail_retornaTrue() {
        boolean existe = repository.existsByEmail("alice@teste.com");
        assertTrue(existe);
    }

    @Test
    @DisplayName("Deve retornar false se o Email não existir")
    void existsByEmail_retornaFalse() {
        boolean existe = repository.existsByEmail("naoexiste@teste.com");
        assertFalse(existe);
    }

    // --- Testes de Busca por Campo Único ---

    @Test
    @DisplayName("Deve buscar Candidato por email e retornar Optional presente")
    void findByEmail_retornaCandidato() {
        Optional<Candidato> encontrado = repository.findByEmail("alice@teste.com");

        assertTrue(encontrado.isPresent());
        assertThat(encontrado.get().getNome()).isEqualTo("Alice Teste");
    }

    @Test
    @DisplayName("Deve buscar Candidato por email e retornar Optional vazio")
    void findByEmail_retornaVazio() {
        Optional<Candidato> encontrado = repository.findByEmail("naoexiste@teste.com");
        assertTrue(encontrado.isEmpty());
    }

    // --- Testes de Busca por Status ---

    @Test
    @DisplayName("Deve buscar candidatos pelo status CANDIDATO")
    void findByStatus_retornaListaCorreta() {
        List<Candidato> lista = repository.findByStatus(StatusCandidato.CANDIDATO);

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertThat(lista.get(0).getEmail()).isEqualTo("alice@teste.com");
    }

    @Test
    @DisplayName("Deve buscar candidatos pelo status TRIAGEM")
    void findByStatus_retornaOutraLista() {
        List<Candidato> lista = repository.findByStatus(StatusCandidato.TRIAGEM);

        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertThat(lista.get(0).getEmail()).isEqualTo("bob@teste.com");
    }

    @Test
    @DisplayName("Deve retornar lista vazia para status sem candidatos")
    void findByStatus_retornaVazio() {
        List<Candidato> lista = repository.findByStatus(StatusCandidato.APROVADO); // Assumindo que APROVADO não foi inserido

        assertTrue(lista.isEmpty());
        assertEquals(0, lista.size());
    }
}