package com.rhgroup.cadastrosrh.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "candidatos",
        indexes = {
                @Index(name = "idx_candidatos_nome", columnList = "nome"),
                @Index(name = "idx_candidatos_status", columnList = "status")
        })
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(length = 11, nullable = false, unique = true)
    private String cpf;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "senha_hash", length = 60, nullable = false)
    private String senhaHash;

    @Column(length = 20)
    private String celular;

    @Column(name = "area_interesse", columnDefinition = "TEXT")
    private String areaInteresse;

    @Column(name = "experiencia_anos", nullable = false)
    private Integer experienciaAnos;

    @Column(name = "pretensao_salarial", precision = 12, scale = 2)
    private BigDecimal pretensaoSalarial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCandidato status;


    @Column(name = "curriculo_url", length = 500)
    private String curriculoUrl;

    @Column(name = "curriculo_nome")
    private String curriculoNome;

    @Column(name = "curriculo_content_type", length = 100)
    private String curriculoContentType;

    @Column(name = "curriculo_tamanho_bytes")
    private Long curriculoTamanhoBytes;

    @Column(name = "curriculo_atualizado_em")
    private LocalDateTime curriculoAtualizadoEm;

    @Column(name = "curriculo_storage", length = 10)
    private String curriculoStorage;

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
}