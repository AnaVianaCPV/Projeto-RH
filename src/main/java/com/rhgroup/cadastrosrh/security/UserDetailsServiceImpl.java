package com.RHgroup.CadastrosRH.security;

import com.RHgroup.CadastrosRH.model.Candidato;
import com.RHgroup.CadastrosRH.repository.CandidatoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CandidatoRepository candidatoRepository;

    // Injeta o repositório para buscar o Candidato
    public UserDetailsServiceImpl(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    /**
     * Carrega o usuário pelo nome de usuário (email, neste caso).
     * @param username O email do Candidato
     * @return UserDetails (o objeto Candidato)
     * @throws UsernameNotFoundException Se o Candidato não for encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Usando o método findByEmail declarado no CandidatoRepository
        Optional<Candidato> candidatoOptional = candidatoRepository.findByEmail(username);

        return candidatoOptional
                .orElseThrow(() -> new UsernameNotFoundException("Candidato com email " + username + " não encontrado."));
    }
}