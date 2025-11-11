package com.rhgroup.cadastrosrh.security;

import com.rhgroup.cadastrosrh.repository.CandidatoRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CandidatoRepository repository;

    public UserDetailsServiceImpl(CandidatoRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var candidato = repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return User.builder()
                .username(candidato.getEmail())
                .password(candidato.getSenhaHash())
                .roles("USER")
                .build();
    }
}
