package com.rhgroup.cadastrosrh.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidatoSenhaDTO {
    @NotBlank
    private String senhaAntiga;
    @NotBlank
    private String senhaNova;
}
