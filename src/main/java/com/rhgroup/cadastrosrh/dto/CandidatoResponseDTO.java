package com.rhgroup.cadastrosrh.dto;

import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CandidatoResponseDTO {
    private UUID id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String celular;
    private String areaInteresse;
    private Integer experienciaAnos;
    private BigDecimal pretensaoSalarial;
    private StatusCandidato status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public static CandidatoResponseDTO fromEntity(Candidato candidato) {
        if (candidato == null) {
            return null;
        }
        var dto = new CandidatoResponseDTO();
        BeanUtils.copyProperties(candidato, dto);
        return dto;
    }
}