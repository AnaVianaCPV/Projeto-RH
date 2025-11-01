package com.RHgroup.CadastrosRH.exception;
// Embora o Spring Data já lide com Optional.orElseThrow(()
// -> new NotFoundException()), é bom ter uma explícita.

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Mapeia para 404
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}