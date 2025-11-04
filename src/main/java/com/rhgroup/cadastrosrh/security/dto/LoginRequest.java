package com.rhgroup.cadastrosrh.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Formato de email inválido")
        String username,

        @NotBlank(message = "A senha é obrigatória")
        String password
) {}
