package com.rhgroup.cadastrosrh.dto;

import com.rhgroup.cadastrosrh.model.StatusCandidato;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CandidatoUpdateDTO {
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
