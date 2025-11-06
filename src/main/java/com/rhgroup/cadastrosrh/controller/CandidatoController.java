package com.rhgroup.cadastrosrh.controller;

import com.rhgroup.cadastrosrh.dto.CandidatoCreateDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoResponseDTO;
import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import com.rhgroup.cadastrosrh.service.CandidatoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/candidatos", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class CandidatoController {

    private final CandidatoService candidatoService;


    public CandidatoController(CandidatoService service) {
        this.candidatoService = service;
    }

    @GetMapping
    public ResponseEntity<Page<CandidatoResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) StatusCandidato status,
            @RequestParam(required = false) Integer experienciaMinima,
            @RequestParam(required = false) Integer experienciaMaxima,
            @PageableDefault(sort = "nome") Pageable pageable,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Page<Candidato> page = candidatoService.listar(
                nome, email, status, experienciaMinima, experienciaMaxima, pageable);

        addPaginationLinks(request, response, page);

        Page<CandidatoResponseDTO> dto = page.map(CandidatoResponseDTO::fromEntity);

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidatoResponseDTO> buscarPorId(@PathVariable UUID id, HttpServletResponse response) {
        Candidato c = candidatoService.buscarPorId(id);

        if (c.getAtualizadoEm() != null) {
            response.setHeader("ETag", "\"" + c.getAtualizadoEm().toEpochSecond(java.time.ZoneOffset.UTC) + "\"");
        }

        return ResponseEntity.ok(CandidatoResponseDTO.fromEntity(c));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CandidatoResponseDTO> criar(@Valid @RequestBody CandidatoCreateDTO dtoIn) {
        CandidatoResponseDTO dtoOut = candidatoService.criar(dtoIn);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(dtoOut.getId()).toUri();

        return ResponseEntity.created(location).body(dtoOut);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CandidatoResponseDTO> atualizar(@PathVariable UUID id,
                                                          @Valid @RequestBody CandidatoCreateDTO dtoIn) {
        CandidatoResponseDTO dtoOut = candidatoService.atualizar(id, dtoIn);

        return ResponseEntity.ok(dtoOut);
    }

    @PatchMapping(value = "/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<CandidatoResponseDTO> patch(@PathVariable UUID id,
                                                      @RequestBody Map<String, Object> patch) {
        Candidato salvo = candidatoService.atualizarParcialmente(id, patch);

        return ResponseEntity.ok(CandidatoResponseDTO.fromEntity(salvo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        candidatoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private void addPaginationLinks(HttpServletRequest req, HttpServletResponse res, Page<?> page) {
        var base = req.getRequestURL().toString();
        StringBuilder links = new StringBuilder();
        if (page.hasPrevious()) {
            links.append("<").append(base).append("?page=").append(page.getNumber() - 1)
                    .append("&size=").append(page.getSize()).append(">; rel=\"prev\", ");
        }
        if (page.hasNext()) {
            links.append("<").append(base).append("?page=").append(page.getNumber() + 1)
                    .append("&size=").append(page.getSize()).append(">; rel=\"next\"");
        }
        if (!links.isEmpty()) res.addHeader("Link", links.toString());
    }
}