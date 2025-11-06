package com.rhgroup.cadastrosrh.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhgroup.cadastrosrh.dto.CandidatoCreateDTO;
import com.rhgroup.cadastrosrh.dto.CandidatoResponseDTO;
import com.rhgroup.cadastrosrh.exception.ConflitoUnicidadeException;
import com.rhgroup.cadastrosrh.exception.RecursoNaoEncontradoException;
import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import com.rhgroup.cadastrosrh.repository.CandidatoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.FeatureDescriptor;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class CandidatoService {

    private static final String FIELD_SENHA = "senha";
    private static final String FIELD_CPF = "cpf";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_ID = "id";
    private static final String FIELD_CRIADO_EM = "criadoEm";

    private final CandidatoRepository repository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    public CandidatoService(CandidatoRepository repository,
                            ObjectMapper objectMapper,
                            PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional
    public CandidatoResponseDTO criar(CandidatoCreateDTO dtoIn) {
        Candidato novoCandidato = new Candidato();
        BeanUtils.copyProperties(dtoIn, novoCandidato, FIELD_ID);

        Candidato salvo = salvarECriptografar(novoCandidato);

        return CandidatoResponseDTO.fromEntity(salvo);
    }

    @Transactional
    public CandidatoResponseDTO atualizar(UUID id, CandidatoCreateDTO dtoIn) {
        Candidato existente = buscarPorId(id);

        Candidato candidatoAtualizado = new Candidato();
        BeanUtils.copyProperties(dtoIn, candidatoAtualizado, FIELD_ID);

        Candidato atualizado = aplicarAtualizacao(existente, candidatoAtualizado);

        return CandidatoResponseDTO.fromEntity(atualizado);
    }


    private Candidato salvarECriptografar(Candidato candidato) {

        if (candidato.getCpf() != null) {
            candidato.setCpf(candidato.getCpf().replaceAll("\\D", ""));
        }

        if (repository.existsByCpf(candidato.getCpf())) {
            throw new ConflitoUnicidadeException("CPF já cadastrado.");
        }
        if (repository.existsByEmail(candidato.getEmail())) {
            throw new ConflitoUnicidadeException("Email já cadastrado.");
        }

        if (candidato.getSenha() != null && !candidato.getSenha().isBlank()) {
            candidato.setSenha(passwordEncoder.encode(candidato.getSenha()));
        }

        return repository.save(candidato);
    }

    private Candidato aplicarAtualizacao(Candidato existente, Candidato candidatoAtualizado) {

        if (candidatoAtualizado.getCpf() != null) {
            candidatoAtualizado.setCpf(candidatoAtualizado.getCpf().replaceAll("\\D", ""));
        }

        if (!Objects.equals(existente.getCpf(), candidatoAtualizado.getCpf())
                && repository.existsByCpf(candidatoAtualizado.getCpf())) {
            throw new ConflitoUnicidadeException("Novo CPF já cadastrado para outro candidato.");
        }

        if (!Objects.equals(existente.getEmail(), candidatoAtualizado.getEmail())
                && repository.existsByEmail(candidatoAtualizado.getEmail())) {
            throw new ConflitoUnicidadeException("Novo Email já cadastrado para outro candidato.");
        }

        if (candidatoAtualizado.getSenha() != null && !candidatoAtualizado.getSenha().isBlank()) {
            candidatoAtualizado.setSenha(passwordEncoder.encode(candidatoAtualizado.getSenha()));
        } else {
            candidatoAtualizado.setSenha(existente.getSenha());
        }

        BeanUtils.copyProperties(candidatoAtualizado, existente, FIELD_ID, FIELD_CRIADO_EM);

        return repository.save(existente);
    }


    public Page<Candidato> listar(String nome,
                                  String email,
                                  StatusCandidato status,
                                  Integer experienciaMinima,
                                  Integer experienciaMaxima,
                                  Pageable pageable) {

        Specification<Candidato> spec = CandidatoSpecification.build(
                nome, email, status, experienciaMinima, experienciaMaxima
        );

        return repository.findAll(spec, pageable);
    }

    public Candidato buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Candidato não encontrado: " + id));
    }


    @Transactional
    public Candidato atualizarParcialmente(UUID id, Map<String, Object> updates) {
        Candidato existente = buscarPorId(id);

        processarSenha(updates);
        processarCpf(updates, existente);
        processarEmail(updates, existente);

        Candidato parcial = objectMapper.convertValue(updates, Candidato.class);
        copyNonNullProperties(parcial, existente, FIELD_ID, FIELD_CRIADO_EM);

        return repository.save(existente);
    }

    private void processarSenha(Map<String, Object> updates) {
        if (updates.containsKey(FIELD_SENHA)) {
            Object raw = updates.get(FIELD_SENHA);
            if (raw instanceof String s && !s.isBlank()) {
                updates.put(FIELD_SENHA, passwordEncoder.encode(s));
            } else {
                updates.remove(FIELD_SENHA);
            }
        }
    }

    private void processarCpf(Map<String, Object> updates, Candidato existente) {
        if (updates.containsKey(FIELD_CPF)) {
            Object raw = updates.get(FIELD_CPF);
            if (raw instanceof String s && !s.isBlank()) {
                String novoCpf = s.replaceAll("\\D", "");
                if (!novoCpf.equals(existente.getCpf()) && repository.existsByCpf(novoCpf)) {
                    throw new ConflitoUnicidadeException("CPF já cadastrado.");
                }
                updates.put(FIELD_CPF, novoCpf);
            } else {
                updates.remove(FIELD_CPF);
            }
        }
    }

    private void processarEmail(Map<String, Object> updates, Candidato existente) {
        if (updates.containsKey(FIELD_EMAIL)) {
            Object raw = updates.get(FIELD_EMAIL);
            if (raw instanceof String s && !s.isBlank()) {
                String novoEmail = s.trim();
                if (!novoEmail.equalsIgnoreCase(existente.getEmail()) && repository.existsByEmail(novoEmail)) {
                    throw new ConflitoUnicidadeException("Email já cadastrado.");
                }
                updates.put(FIELD_EMAIL, novoEmail);
            } else {
                updates.remove(FIELD_EMAIL);
            }
        }
    }


    @Transactional
    public void deletar(UUID id) {
        Candidato existente = buscarPorId(id);
        repository.delete(existente);
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        return Stream.of(src.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }


    private static void copyNonNullProperties(Object src, Object target, String... extraIgnore) {
        String[] nullProps = getNullPropertyNames(src);
        String[] ignore = Stream.concat(Stream.of(nullProps), Stream.of(extraIgnore))
                .distinct()
                .toArray(String[]::new);

        BeanUtils.copyProperties(src, target, ignore);
    }
}