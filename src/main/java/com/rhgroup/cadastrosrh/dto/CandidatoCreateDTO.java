package com.rhgroup.cadastrosrh.dto;

import com.rhgroup.cadastrosrh.model.StatusCandidato;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CandidatoCreateDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 dígitos")
    private String cpf;

    @Past(message = "A data de nascimento deve estar no passado")
    private LocalDate dataNascimento;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String senha;

    private String celular;
    private String areaInteresse;

    @NotNull(message = "A experiência em anos é obrigatória")
    @Min(value = 0, message = "A experiência não pode ser negativa")
    private Integer experienciaAnos;

    @DecimalMin(value = "0.0", message = "A pretensão salarial não pode ser negativa")
    private BigDecimal pretensaoSalarial;

    @NotNull(message = "O status é obrigatório")
    private StatusCandidato status;
}