package com.RHgroup.CadastrosRH.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name= "candidatos")

public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "cpf", length = 11, nullable = false, unique = true)
    private String cpf;

    private LocalDate dataNascimento;

    @Column(nullable = false, unique = true)
    private String email;

    private String celular;

    @Column(name = "area_interesse")
    private String areaInteresse;

    @Column(name = "experiencia_anos", nullable = false)
    private Integer experienciaAnos;

    private BigDecimal pretensaoSalarial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCandidato status; // Tipo correto: StatusCandidato


    // Campos de auditoria
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void aoCriar() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void aoAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }

    // **A LINHA ABAIXO FOI REMOVIDA PARA CORRIGIR O ERRO:**
    // public void setStatus(String ativo) {}
}