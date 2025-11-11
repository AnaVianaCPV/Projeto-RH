package com.rhgroup.cadastrosrh.dto;

import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CandidatoCreateDTO {

    @NotBlank
    private String nome;

    @NotBlank
    @Size(min = 11, max = 11)
    private String cpf;

    @Past
    private LocalDate dataNascimento;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String senha;

    private String celular;
    private String areaInteresse;

    @NotNull
    @Min(0)
    private Integer experienciaAnos;

    @DecimalMin("0.0")
    private BigDecimal pretensaoSalarial;

    @NotNull
    private StatusCandidato status;

    public Candidato toEntity(String senhaHash) {
        return Candidato.builder()
                .nome(this.nome)
                .cpf(this.cpf)
                .email(this.email)
                .senhaHash(senhaHash)
                .dataNascimento(this.dataNascimento)
                .celular(this.celular)
                .areaInteresse(this.areaInteresse)
                .experienciaAnos(this.experienciaAnos)
                .pretensaoSalarial(this.pretensaoSalarial)
                .status(this.status)
                .build();
    }
}
