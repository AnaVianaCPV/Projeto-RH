package com.RHgroup.CadastrosRH.controller;

import com.RHgroup.CadastrosRH.dto.CandidatoCreateDTO;
import com.RHgroup.CadastrosRH.dto.CandidatoResponseDTO;
import com.RHgroup.CadastrosRH.model.Candidato;
import com.RHgroup.CadastrosRH.model.StatusCandidato;
import com.RHgroup.CadastrosRH.service.CandidatoService;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping(value = "/candidatos", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class CandidatoController {

    private final CandidatoService candidatoService;

    public CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = candidatoService;
    }

    // --- LISTAR PAGINADO COM FILTROS (GET /candidatos) ---
    @GetMapping
    public ResponseEntity<Page<CandidatoResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) StatusCandidato status,
            @RequestParam(required = false) Integer experienciaMinima,
            @RequestParam(required = false) Integer experienciaMaxima,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable
    ) {
        Page<Candidato> candidatosPage = candidatoService.listar(
                nome, email, status, experienciaMinima, experienciaMaxima, pageable);

        // Mapeia a Page de Candidato para Page de CandidatoResponseDTO
        Page<CandidatoResponseDTO> dtoPage = candidatosPage.map(candidato -> {
            CandidatoResponseDTO dto = new CandidatoResponseDTO();
            BeanUtils.copyProperties(candidato, dto);
            return dto;
        });

        return ResponseEntity.ok(dtoPage);
    }

    // --- BUSCAR POR ID (GET /candidatos/{id}) ---
    @GetMapping("/{id}")
    public ResponseEntity<CandidatoResponseDTO> buscarPorId(@PathVariable UUID id) {
        Candidato candidato = candidatoService.buscarPorId(id);

        CandidatoResponseDTO dto = new CandidatoResponseDTO();
        BeanUtils.copyProperties(candidato, dto);

        return ResponseEntity.ok(dto);
    }

    // --- CRIAR (POST /candidatos) ---
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CandidatoResponseDTO> criar(@RequestBody CandidatoCreateDTO dto) {
        Candidato novoCandidato = new Candidato();
        BeanUtils.copyProperties(dto, novoCandidato, "id"); // Ignora o ID do DTO

        Candidato salvo = candidatoService.salvar(novoCandidato);

        // Cria a URI do recurso criado para retornar no header Location (Status 201 Created)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();

        CandidatoResponseDTO responseDto = new CandidatoResponseDTO();
        BeanUtils.copyProperties(salvo, responseDto);

        return ResponseEntity.created(location).body(responseDto);
    }

    // --- ATUALIZAR TOTAL (PUT /candidatos/{id}) ---
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CandidatoResponseDTO> atualizar(@PathVariable UUID id, @RequestBody CandidatoCreateDTO dto) {
        Candidato candidatoAtualizado = new Candidato();
        BeanUtils.copyProperties(dto, candidatoAtualizado); // Copia todas as propriedades do DTO

        Candidato salvo = candidatoService.atualizar(id, candidatoAtualizado);

        CandidatoResponseDTO responseDto = new CandidatoResponseDTO();
        BeanUtils.copyProperties(salvo, responseDto);

        return ResponseEntity.ok(responseDto);
    }

    // --- ATUALIZAR PARCIAL (PATCH /candidatos/{id}) ---
    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<CandidatoResponseDTO> atualizarParcialmente(@PathVariable UUID id, @RequestBody Map<String, Object> jsonMergePatch) {
        Candidato salvo = candidatoService.atualizarParcialmente(id, jsonMergePatch);

        CandidatoResponseDTO responseDto = new CandidatoResponseDTO();
        BeanUtils.copyProperties(salvo, responseDto);

        return ResponseEntity.ok(responseDto);
    }

    // --- DELETAR (DELETE /candidatos/{id}) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        candidatoService.deletar(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}