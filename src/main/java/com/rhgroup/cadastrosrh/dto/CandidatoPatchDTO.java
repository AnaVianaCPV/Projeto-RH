package com.rhgroup.cadastrosrh.dto;

import com.rhgroup.cadastrosrh.model.StatusCandidato;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CandidatoPatchDTO {
    private String nome;
    private String celular;
    private String areaInteresse;
    private Integer experienciaAnos;
    private BigDecimal pretensaoSalarial;
    private StatusCandidato status;
}
