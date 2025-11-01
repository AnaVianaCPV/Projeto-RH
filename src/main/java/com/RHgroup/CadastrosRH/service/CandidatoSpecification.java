package com.RHgroup.CadastrosRH.service;

import com.RHgroup.CadastrosRH.model.Candidato;
import com.RHgroup.CadastrosRH.model.StatusCandidato;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CandidatoSpecification {

    // Previne instâncias utilitárias
    private CandidatoSpecification() {}

    /**
     * Constrói a Specification baseada nos filtros fornecidos.
     * @param nome Nome para busca parcial (LIKE, case-insensitive)
     * @param email Email exato
     * @param status Status do candidato
     * @param experienciaMin Anos de experiência mínima
     * @param experienciaMax Anos de experiência máxima
     * @return Specification combinada
     */
    public static Specification<Candidato> build(
            String nome,
            String email,
            StatusCandidato status,
            Integer experienciaMin,
            Integer experienciaMax) {

        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtro por Nome (LIKE %termo%)
            if (StringUtils.hasText(nome)) {
                predicates.add(builder.like(
                        builder.lower(root.get("nome")),
                        "%" + nome.toLowerCase() + "%"
                ));
            }

            // 2. Filtro por Email (EXATO)
            if (StringUtils.hasText(email)) {
                predicates.add(builder.equal(root.get("email"), email));
            }

            // 3. Filtro por Status (EXATO)
            if (Objects.nonNull(status)) {
                predicates.add(builder.equal(root.get("status"), status));
            }

            // 4. Filtro por Faixa de Experiência (BETWEEN)
            if (Objects.nonNull(experienciaMin) || Objects.nonNull(experienciaMax)) {

                Integer min = Objects.nonNull(experienciaMin) ? experienciaMin : 0;
                Integer max = Objects.nonNull(experienciaMax) ? experienciaMax : Integer.MAX_VALUE;

                predicates.add(builder.between(root.get("experienciaAnos"), min, max));
            }

            // Combina todos os predicados com AND
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}