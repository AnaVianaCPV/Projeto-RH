package com.rhgroup.cadastrosrh.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    pd.setTitle("Dados inválidos");
    pd.setDetail("Verifique os campos e tente novamente");
    pd.setProperty("errors", ex.getBindingResult().getFieldErrors().stream()
            .map(this::toError)
            .toList());
    pd.setProperty("path", req.getRequestURI());
    return pd;
  }

  @ExceptionHandler(NotFoundException.class)
  public ProblemDetail handleNotFound(NotFoundException ex, HttpServletRequest req) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    pd.setTitle("Recurso não encontrado");
    pd.setDetail(ex.getMessage());
    pd.setProperty("path", req.getRequestURI());
    return pd;
  }

  @ExceptionHandler(ConflitoUnicidadeException.class)
  public ProblemDetail handleConflict(ConflitoUnicidadeException ex, HttpServletRequest req) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    pd.setTitle("Conflito de dados");
    pd.setDetail(ex.getMessage());
    pd.setProperty("path", req.getRequestURI());
    return pd;
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail handleIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    pd.setTitle("Violação de integridade");
    pd.setDetail("Violação de integridade referencial no banco de dados.");
    pd.setProperty("path", req.getRequestURI());
    return pd;
  }

  private Map<String, Object> toError(FieldError fe) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("field", fe.getField());
    m.put("message", Optional.ofNullable(fe.getDefaultMessage()).orElse("Valor inválido"));
    Object rejected = fe.getRejectedValue();
    if (rejected != null) m.put("rejectedValue", rejected);
    return m;
  }
}