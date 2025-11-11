package com.rhgroup.cadastrosrh.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflitoUnicidadeException extends RuntimeException {
    public ConflitoUnicidadeException(String mensagem) {
        super(mensagem);
    }
}