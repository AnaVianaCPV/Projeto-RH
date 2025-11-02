package com.RHgroup.CadastrosRH.repository;

import com.RHgroup.CadastrosRH.model.Candidato;
import com.RHgroup.CadastrosRH.model.StatusCandidato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CandidatoRepository extends JpaRepository<Candidato, UUID>,
        JpaSpecificationExecutor<Candidato> {

    // --- Métodos para Validação de Unicidade ---
    /**
     * Verifica se existe um candidato com o CPF fornecido.
     */
    boolean existsByCpf(String cpf);

    /**
     * Verifica se existe um candidato com o Email fornecido.
     */
    boolean existsByEmail(String email);

    // --- Consultas de Filtro Adaptadas com Pageable ---
    Page<Candidato> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Candidato> findByEmail(String email, Pageable pageable);
    Page<Candidato> findByStatus(StatusCandidato status, Pageable pageable);

    @Query("SELECT c FROM Candidato c WHERE c.experienciaAnos BETWEEN :min AND :max")
    Page<Candidato> findByExperienciaAnosBetween(@Param("min") int min,
                                                 @Param("max") int max,
                                                 Pageable pageable);
}