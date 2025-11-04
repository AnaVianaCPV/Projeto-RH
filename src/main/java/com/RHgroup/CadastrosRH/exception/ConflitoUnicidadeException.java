package com.RHgroup.CadastrosRH.exception;
// Classe criada  para lidar com conflitos de unicidade (409)

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // Mapeia para 409
public class ConflitoUnicidadeException extends RuntimeException {
    public ConflitoUnicidadeException(String mensagem) {
        super(mensagem);
    }
}