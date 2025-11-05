package com.rhgroup.cadastrosrh.repository;

import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.UUID;

@Repository
public interface CandidatoRepository extends JpaRepository<Candidato, UUID>,
        JpaSpecificationExecutor<Candidato> {

    // --- Métodos para Validação de Unicidade ---
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);

    // --- Método para Spring Security (retorna um único Candidato por email) ---
    /**
     * Busca um candidato por email, usado pelo Spring Security.
     */
    Optional<Candidato> findByEmail(String email);

    // --- Consultas de Filtro Adaptadas com Pageable ---
    Page<Candidato> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    // Método para busca paginada (o Spring distingue este do Optional<Candidato>)
    Page<Candidato> findByEmail(String email, Pageable pageable);

    Page<Candidato> findByStatus(StatusCandidato status, Pageable pageable);

    @Query("SELECT c FROM Candidato c WHERE c.experienciaAnos BETWEEN :min AND :max")
    Page<Candidato> findByExperienciaAnosBetween(@Param("min") int min,
                                                 @Param("max") int max,
                                                 Pageable pageable);
}