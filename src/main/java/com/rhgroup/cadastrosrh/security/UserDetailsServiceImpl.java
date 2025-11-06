package com.rhgroup.cadastrosrh.security;

import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.repository.CandidatoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CandidatoRepository candidatoRepository;

    public UserDetailsServiceImpl(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Candidato candidato = candidatoRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Candidato com email " + username + " não encontrado."));

        return new CandidatoUserDetails(candidato);
    }

}