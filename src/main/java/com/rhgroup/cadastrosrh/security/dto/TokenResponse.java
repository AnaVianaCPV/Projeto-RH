package com.rhgroup.cadastrosrh.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de resposta que contém o token JWT gerado após login bem-sucedido.
 */
@Schema(description = "Resposta de autenticação contendo o token JWT")
public record TokenResponse(

        @Schema(description = "Token de acesso JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String access_token,

        @Schema(description = "Tipo de token, geralmente 'Bearer'", example = "Bearer")
        String token_type,

        @Schema(description = "Tempo de expiração em segundos", example = "3600")
        long expires_in
) {}
