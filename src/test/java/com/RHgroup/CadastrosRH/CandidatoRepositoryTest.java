package com.RHgroup.CadastrosRH;

import com.RHgroup.CadastrosRH.model.Candidato;
import com.RHgroup.CadastrosRH.model.StatusCandidato;
import com.RHgroup.CadastrosRH.repository.CandidatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

@DataJpaTest
// VAI USAR A CONFIGURAÇÃO REAL DO application.properties (PostgreSQL/rhdb)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DisplayName("Testes de Integração para CandidatoRepository")
class CandidatoRepositoryTest {

    @Autowired
    private CandidatoRepository repository;

    private Candidato candidatoBase;
    private Pageable pageable;

    @BeforeEach
    void setup() {
        // Limpar antes de cada teste para garantir isolamento
        repository.deleteAll();

        candidatoBase = new Candidato(
                null,
                "João Teste",
                "11122233344",
                LocalDate.of(1990, 1, 1),
                "joao.teste@repo.com",
                "99887766",
                "Backend Java",
                5,
                new BigDecimal("6000.00"),
                StatusCandidato.ATIVO,
                null, // PrePersist cuida disso
                null  // PrePersist cuida disso
        );
        pageable = PageRequest.of(0, 10);

        // Persistir o candidato base para testes de busca e unicidade
        repository.save(candidatoBase);
    }

    // --- Testes de Persistência e Busca ---

    @Test
    void save_DevePersistirCandidatoComSucesso() {
        // Arrange
        Candidato novoCandidato = new Candidato(
                null,
                "Maria Nova",
                "55566677788",
                LocalDate.of(1995, 5, 5),
                "maria.nova@repo.com",
                "11223344",
                "Frontend React",
                2,
                new BigDecimal("4500.00"),
                StatusCandidato.CANDIDATO,
                null, null
        );

        // Act
        Candidato salvo = repository.save(novoCandidato);

        // Assert
        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Maria Nova");
        assertThat(salvo.getCriadoEm()).isNotNull();
    }

    @Test
    void findById_DeveRetornarCandidato_QuandoExistir() {
        // Act
        Optional<Candidato> encontrado = repository.findById(candidatoBase.getId());

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getCpf()).isEqualTo(candidatoBase.getCpf());
    }

    // --- Testes de Unicidade (Regra de Negócio) ---

    @Test
    void existsByCpf_DeveRetornarTrue_QuandoCPFJaExiste() {
        // Act
        boolean existe = repository.existsByCpf(candidatoBase.getCpf());

        // Assert
        assertThat(existe).isTrue();
    }

    @Test
    void existsByCpf_DeveRetornarFalse_QuandoCPFNaoExiste() {
        // Act
        boolean existe = repository.existsByCpf("99999999999");

        // Assert
        assertThat(existe).isFalse();
    }

    @Test
    void existsByEmail_DeveRetornarTrue_QuandoEmailJaExiste() {
        // Act
        boolean existe = repository.existsByEmail(candidatoBase.getEmail());

        // Assert
        assertThat(existe).isTrue();
    }

    // --- Testes dos Métodos de Filtro Adaptados (Pageable) ---

    @Test
    void findByNomeContainingIgnoreCase_DeveRetornarPagina_ComFiltroPorNome() {
        // Act
        Page<Candidato> pagina = repository.findByNomeContainingIgnoreCase("joão", pageable);

        // Assert
        assertThat(pagina).isNotEmpty();
        assertThat(pagina.getContent().get(0).getNome()).isEqualTo("João Teste");
    }

    @Test
    void findByStatus_DeveRetornarPagina_ComFiltroPorStatus() {
        // Act
        Page<Candidato> pagina = repository.findByStatus(StatusCandidato.ATIVO, pageable);

        // Assert
        assertThat(pagina).isNotEmpty();
        assertThat(pagina.getContent().get(0).getStatus()).isEqualTo(StatusCandidato.ATIVO);
    }

    @Test
    void findByExperienciaAnosBetween_DeveRetornarPagina_ComFaixaCorreta() {
        // Act
        Page<Candidato> pagina = repository.findByExperienciaAnosBetween(4, 6, pageable);

        // Assert
        assertThat(pagina).isNotEmpty();
        assertThat(pagina.getContent().get(0).getExperienciaAnos()).isEqualTo(5);
    }

    // --- Teste de Violação de Chave Única no BD (DoD Implícito) ---

    @Test
    void save_DeveLancarExcecao_AoTentarSalvarCPFDuplicado() {
        // Arrange: Criar um candidato com o mesmo CPF
        Candidato duplicado = new Candidato(
                null,
                "Candidato Dup",
                candidatoBase.getCpf(), // Mesmo CPF
                LocalDate.of(2000, 1, 1),
                "outro@email.com", // Email diferente, mas o CPF causará o erro
                "123456",
                "QA",
                1,
                new BigDecimal("3000.00"),
                StatusCandidato.REPROVADO,
                null, null
        );

        // Act & Assert
        // A exceção DataIntegrityViolationException é lançada pela JPA/Hibernate
        assertThatThrownBy(() -> repository.saveAndFlush(duplicado))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}