package com.RHgroup.CadastrosRH;// <--- PACOTE CORRETO

import com.RHgroup.CadastrosRH.model.Candidato;
import com.RHgroup.CadastrosRH.model.StatusCandidato;
import com.RHgroup.CadastrosRH.repository.CandidatoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CandidatoRepositoryTest {

    @Autowired
    private CandidatoRepository repository;

    private Candidato c1;
    private Candidato c2;
    private Candidato c3;

    private final Pageable pageableDefault = PageRequest.of(0, 10);

    @BeforeEach
    void setup() {
        repository.deleteAll();

        // 1. Candidato Alice (CANDIDATO, 5 anos)
        c1 = new Candidato();
        c1.setNome("Alice Silva");
        c1.setEmail("alice@rh.com");
        c1.setStatus(StatusCandidato.CANDIDATO);
        c1.setExperienciaAnos(5);
        repository.save(c1);

        // 2. Candidato Bob (TRIAGEM, 10 anos)
        c2 = new Candidato();
        c2.setNome("Bob Santos");
        c2.setEmail("bob@rh.com");
        c2.setStatus(StatusCandidato.TRIAGEM);
        c2.setExperienciaAnos(10);
        repository.save(c2);

        // 3. Candidato Carlos (CANDIDATO, 1 ano)
        c3 = new Candidato();
        c3.setNome("Carlos Pereira");
        c3.setEmail("carlos@outra.com");
        c3.setStatus(StatusCandidato.CANDIDATO);
        c3.setExperienciaAnos(1);
        repository.save(c3);
    }

    @Test
    void shouldFilterByNomeContainingIgnoreCaseAndReturnPage() {
        Page<Candidato> foundPage = repository.findByNomeContainingIgnoreCase("li", pageableDefault);
        assertThat(foundPage.getTotalElements()).isEqualTo(1);
        assertThat(foundPage.getContent().get(0).getNome()).isEqualTo("Alice Silva");
    }

    @Test
    void shouldFilterByEmailAndReturnPage() {
        Page<Candidato> foundPage = repository.findByEmail("bob@rh.com", pageableDefault);
        assertThat(foundPage.getTotalElements()).isEqualTo(1);
        assertThat(foundPage.getContent().get(0).getNome()).isEqualTo("Bob Santos");
    }

    @Test
    void shouldFilterByStatusAndReturnPage() {
        Page<Candidato> foundPage = repository.findByStatus(StatusCandidato.CANDIDATO, pageableDefault);
        assertThat(foundPage.getTotalElements()).isEqualTo(2);
    }

    @Test
    void shouldFilterByExperienciaAnosBetweenAndReturnPage() {
        Page<Candidato> foundPage = repository.findByExperienciaAnosBetween(3, 7, pageableDefault);
        assertThat(foundPage.getTotalElements()).isEqualTo(1);
        assertThat(foundPage.getContent().get(0).getNome()).isEqualTo("Alice Silva");
    }

    @Test
    void shouldFilterCombinedUsingExampleAndReturnPage() {
        Candidato probe = new Candidato();
        probe.setNome("silva");
        probe.setStatus(StatusCandidato.CANDIDATO);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Page<Candidato> foundPage = repository.findAll(Example.of(probe, matcher), pageableDefault);

        assertThat(foundPage.getTotalElements()).isEqualTo(1);
        assertThat(foundPage.getContent().get(0).getNome()).isEqualTo("Alice Silva");
    }
}