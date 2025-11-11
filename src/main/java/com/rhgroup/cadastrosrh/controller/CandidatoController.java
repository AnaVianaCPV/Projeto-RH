package com.rhgroup.cadastrosrh.controller;

import com.rhgroup.cadastrosrh.dto.CandidatoCreateDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoResponseDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoUpdateDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoPatchDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoSenhaDTO;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import com.rhgroup.cadastrosrh.service.CandidatoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/candidatos")
public class CandidatoController {

    private final CandidatoService service;

    public CandidatoController(CandidatoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CandidatoResponseDTO> criar(@RequestBody CandidatoCreateDTO dto) {
        CandidatoResponseDTO novo = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @GetMapping
    public ResponseEntity<List<CandidatoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidatoResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CandidatoResponseDTO>> buscarPorStatus(@PathVariable StatusCandidato status) {
        return ResponseEntity.ok(service.buscarPorStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidatoResponseDTO> atualizar(
            @PathVariable UUID id,
            @RequestBody CandidatoUpdateDTO dto) {
        CandidatoResponseDTO atualizado = service.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CandidatoResponseDTO> atualizarParcial(
            @PathVariable UUID id,
            @RequestBody CandidatoPatchDTO dto) {
        CandidatoResponseDTO atualizado = service.atualizarParcial(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @PatchMapping("/{id}/senha")
    public ResponseEntity<Void> atualizarSenha(
            @PathVariable UUID id,
            @RequestBody CandidatoSenhaDTO dto) {
        service.atualizarSenha(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}