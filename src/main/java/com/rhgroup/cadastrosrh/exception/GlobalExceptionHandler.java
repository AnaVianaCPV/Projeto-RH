package com.RHgroup.CadastrosRH.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice // diz pro Spring que esse arquivo cuida dos erros da API
public class GlobalExceptionHandler {

  //  Quando algo dá errado por erro de validação (ex: campo vazio ou email inválido)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
    // Pega os erros de campo e transforma numa lista mais fácil de ler
    List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new FieldErrorDetail(
                    error.getField(),
                    error.getDefaultMessage()
            ))
            .collect(Collectors.toList());

    // Cria o corpo da resposta
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Erro de validação");
    problem.setDetail("Um ou mais campos estão inválidos.");
    problem.setType(URI.create("about:blank#bad-request"));
    problem.setProperty("timestamp", OffsetDateTime.now());
    problem.setProperty("fields", fieldErrors);

    return problem;
  }

  // Quando o sistema tenta salvar algo duplicado (ex: mesmo CPF ou e-mail)
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail handleDuplicate(DataIntegrityViolationException ex) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    problem.setTitle("Conflito de dados");
    problem.setDetail("Já existe um registro com esse e-mail ou CPF.");
    problem.setType(URI.create("about:blank#conflict"));
    problem.setProperty("timestamp", OffsetDateTime.now());
    return problem;
  }

  //Quando algo inesperado acontece no sistema
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneric(Exception ex) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problem.setTitle("Erro interno");
    problem.setDetail("Ocorreu um erro inesperado. Tente novamente mais tarde.");
    problem.setType(URI.create("about:blank#internal-error"));
    problem.setProperty("timestamp", OffsetDateTime.now());
    return problem;
  }
}
