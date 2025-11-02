package com.RHgroup.CadastrosRH.dto;

import com.RHgroup.CadastrosRH.model.StatusCandidato;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CandidatoCreateDTO {
    // Validações (ex: @NotBlank, @Email) seriam adicionadas aqui em um projeto real.
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String celular;
    private String areaInteresse;
    private Integer experienciaAnos;
    private BigDecimal pretensaoSalarial;
    private StatusCandidato status;
}