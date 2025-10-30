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

    // --- Consultas de Filtro Adaptadas com Pageable ---

    /**
     * Busca candidatos por nome contendo o termo (case-insensitive) com paginação.
     */
    Page<Candidato> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    /**
     * Busca candidatos por e-mail exato com paginação.
     */
    Page<Candidato> findByEmail(String email, Pageable pageable);

    /**
     * Busca candidatos por status (Enum) com paginação.
     * Note que agora estamos usando o StatusCandidato do seu modelo.
     */
    Page<Candidato> findByStatus(StatusCandidato status, Pageable pageable);

    /**
     * Busca candidatos com experiência dentro de uma faixa especificada com paginação.
     * Usando Pageable para o retorno paginado.
     */
    @Query("SELECT c FROM Candidato c WHERE c.experienciaAnos BETWEEN :min AND :max")
    Page<Candidato> findByExperienciaAnosBetween(@Param("min") int min,
                                                 @Param("max") int max,
                                                 Pageable pageable);

    // --- Uso do JpaSpecificationExecutor ---

    // Não precisamos de métodos de filtro combinados aqui!
    // O método 'findAll(Specification<T> spec, Pageable pageable)'
    // herdado de JpaSpecificationExecutor já suporta a combinação
    // de *TODOS* os filtros do seu Controller (nome, email, status, faixa de exp.)
    // de forma dinâmica, além da paginação.
}