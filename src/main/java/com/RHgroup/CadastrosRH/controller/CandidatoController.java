package com.RHgroup.CadastrosRH.controller;


import com.RHgroup.CadastrosRH.model.StatusCandidato;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

import java.net.URI;
//import java.net.URI;
// Ele serve pra criar os identificadores dos tipos de erro (type no Problem Detail))
//Não apagar, ele faz isso indiretamente

/**
 * Esta classe define a estrutura base dos endpoints (sem implementar regras de negócio),
 * Status de retorno esperados (quando implementado):
 * 200 OK | 201 Created | 204 No Content | 400 Bad Request | 404 Not Found | 409 Conflict
 */

@RestController
@RequestMapping(value = "/candidatos", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*") // libera requisições de qualquer origem para testes locais
public class CandidatoController {

    @GetMapping
    public ResponseEntity<Void> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) StatusCandidato status,
            @RequestParam(required = false) Integer experienciaMinima,
            @RequestParam(required = false) Integer experienciaMaxima,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable
    ) {
        // Filtros e paginação serão tratados no Service posteriormente
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<Void> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> criar(@RequestBody String body) {
        // Quando implementar, trocar "String body" por "CandidatoCreateDTO"
        // e retorne 201 Created + Location
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }


    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> atualizar(@PathVariable UUID id, @RequestBody String body) {
        // Trocar por CandidatoUpdateDTO quando for implementado
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Void> atualizarParcialmente(@PathVariable UUID id, @RequestBody String jsonMergePatch) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
