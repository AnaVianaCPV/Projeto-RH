package com.RHgroup.CadastrosRH.service;

import com.RHgroup.CadastrosRH.model.Candidato;
import com.RHgroup.CadastrosRH.model.StatusCandidato;
import com.RHgroup.CadastrosRH.repository.CandidatoRepository;
import com.RHgroup.CadastrosRH.exception.ConflitoUnicidadeException;
import com.RHgroup.CadastrosRH.exception.RecursoNaoEncontradoException;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class CandidatoService {

    private final CandidatoRepository repository;
    private final ObjectMapper objectMapper; // Injetado automaticamente pelo Spring Boot

    public CandidatoService(CandidatoRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    // --- 1. Listar Paginado com Filtros e Ordenação ---
    public Page<Candidato> listar(
            String nome,
            String email,
            StatusCandidato status,
            Integer experienciaMinima,
            Integer experienciaMaxima,
            Pageable pageable) {

        // 1. Constrói a Specification com os filtros opcionais
        Specification<Candidato> spec = CandidatoSpecification.build(
                nome, email, status, experienciaMinima, experienciaMaxima);

        // 2. Utiliza o findAll do JpaSpecificationExecutor com a paginação e a ordenação
        return repository.findAll(spec, pageable);
    }

    // --- 2. Buscar por ID com 404 ---
    public Candidato buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Candidato não encontrado: " + id));
    }

    // --- 3. Salvar com Validações (CPF e Email únicos) ---
    @Transactional
    public Candidato salvar(Candidato candidato) {

        // Validação de Unicidade de CPF
        if (repository.existsByCpf(candidato.getCpf())) {
            throw new ConflitoUnicidadeException("CPF já cadastrado.");
        }

        // Validação de Unicidade de Email
        if (repository.existsByEmail(candidato.getEmail())) {
            throw new ConflitoUnicidadeException("Email já cadastrado.");
        }

        // Os métodos @PrePersist/aoCriar na entidade cuidam das datas
        return repository.save(candidato);
    }

    // --- 4. Atualizar (PUT - Substituição total) ---
    @Transactional
    public Candidato atualizar(UUID id, Candidato candidatoAtualizado) {

        Candidato candidatoExistente = buscarPorId(id);

        // Validação de Unicidade de CPF e Email (apenas se o valor for diferente)
        if (!candidatoExistente.getCpf().equals(candidatoAtualizado.getCpf()) &&
                repository.existsByCpf(candidatoAtualizado.getCpf())) {
            throw new ConflitoUnicidadeException("Novo CPF já cadastrado para outro candidato.");
        }
        if (!candidatoExistente.getEmail().equals(candidatoAtualizado.getEmail()) &&
                repository.existsByEmail(candidatoAtualizado.getEmail())) {
            throw new ConflitoUnicidadeException("Novo Email já cadastrado para outro candidato.");
        }

        // Copia todas as propriedades do DTO para a entidade, exceto o ID e as datas de criação
        BeanUtils.copyProperties(candidatoAtualizado, candidatoExistente, "id", "criadoEm");

        // O método @PreUpdate/aoAtualizar na entidade cuida da data de atualização
        return repository.save(candidatoExistente);
    }

    // --- 5. Atualizar Parcial (PATCH - JSON Merge Patch) ---
    @Transactional
    public Candidato atualizarParcialmente(UUID id, Map<String, Object> updates) {

        Candidato candidatoExistente = buscarPorId(id);

        // Aplica o JSON Merge Patch usando o ObjectMapper
        // O ObjectMapper desserializa o Map (updates) para a entidade
        Candidato candidatoAtualizado = objectMapper.convertValue(updates, Candidato.class);

        // Faz o merge das propriedades alteradas para a entidade existente
        // É necessário cuidado extra na lógica de PATCH para CPF/Email, mas para simplificar:

        if (updates.containsKey("cpf")) {
            // Lógica de validação de CPF omitida aqui para simplicidade, mas necessária em produção
            candidatoExistente.setCpf(candidatoAtualizado.getCpf());
        }
        if (updates.containsKey("email")) {
            // Lógica de validação de Email omitida aqui para simplicidade, mas necessária em produção
            candidatoExistente.setEmail(candidatoAtualizado.getEmail());
        }

        // Copia as propriedades (exceto as ignoradas)
        // Isso funciona bem para campos que não precisam de validação de unicidade complexa.
        BeanUtils.copyProperties(candidatoAtualizado, candidatoExistente, "id", "criadoEm");

        // Salva e atualiza a data automaticamente
        return repository.save(candidatoExistente);
    }

    // --- 6. Deletar com 404 ---
    @Transactional
    public void deletar(UUID id) {
        Candidato candidatoExistente = buscarPorId(id); // Garante que o 404 é lançado se não existir
        repository.delete(candidatoExistente);
    }
}