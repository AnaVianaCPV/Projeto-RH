package com.rhgroup.cadastrosrh.service;

import com.rhgroup.cadastrosrh.dto.*;
import com.rhgroup.cadastrosrh.exception.NotFoundException;
import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import com.rhgroup.cadastrosrh.repository.CandidatoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandidatoService {

    private static final String NOT_FOUND_MSG = "Candidato não encontrado";

    private final CandidatoRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CandidatoResponseDTO criar(CandidatoCreateDTO dto) {
        String senhaHash = passwordEncoder.encode(dto.getSenha());
        Candidato candidato = dto.toEntity(senhaHash);
        repository.save(candidato);
        return CandidatoResponseDTO.fromEntity(candidato);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CandidatoResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(CandidatoResponseDTO::fromEntity)
                .toList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public CandidatoResponseDTO buscarPorId(UUID id) {
        Candidato candidato = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MSG));
        return CandidatoResponseDTO.fromEntity(candidato);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CandidatoResponseDTO> buscarPorStatus(StatusCandidato status) {
        return repository.findByStatus(status).stream()
                .map(CandidatoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CandidatoResponseDTO atualizar(UUID id, CandidatoUpdateDTO dto) {
        Candidato existente = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MSG));

        BeanUtils.copyProperties(dto, existente, "id", "senhaHash", "criadoEm", "email", "cpf");

        repository.save(existente);
        return CandidatoResponseDTO.fromEntity(existente);
    }

    @Transactional
    public CandidatoResponseDTO atualizarParcial(UUID id, CandidatoPatchDTO dto) {
        Candidato existente = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MSG));
        if (dto.getNome() != null) existente.setNome(dto.getNome());
        if (dto.getCelular() != null) existente.setCelular(dto.getCelular());
        if (dto.getAreaInteresse() != null) existente.setAreaInteresse(dto.getAreaInteresse());
        if (dto.getExperienciaAnos() != null) existente.setExperienciaAnos(dto.getExperienciaAnos());
        if (dto.getPretensaoSalarial() != null) existente.setPretensaoSalarial(dto.getPretensaoSalarial());
        if (dto.getStatus() != null) existente.setStatus(dto.getStatus());
        repository.save(existente);
        return CandidatoResponseDTO.fromEntity(existente);
    }

    @Transactional
    public void deletar(UUID id) {
        Candidato c = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MSG));
        repository.delete(c);
    }

    @Transactional
    public void atualizarSenha(UUID id, CandidatoSenhaDTO dto) {
        Candidato c = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MSG));
        if (!passwordEncoder.matches(dto.getSenhaAntiga(), c.getSenhaHash())) {
            throw new IllegalArgumentException("Senha antiga incorreta!");
        }
        c.setSenhaHash(passwordEncoder.encode(dto.getSenhaNova()));
        repository.save(c);
    }
}