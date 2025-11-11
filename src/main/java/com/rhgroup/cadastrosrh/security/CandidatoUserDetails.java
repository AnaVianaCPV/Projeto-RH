package com.rhgroup.cadastrosrh.security;

import com.rhgroup.cadastrosrh.model.Candidato;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CandidatoUserDetails implements UserDetails {

    private final Candidato candidato;

    public CandidatoUserDetails(Candidato candidato) {
        this.candidato = candidato;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return candidato.getSenhaHash();
    }

    @Override
    public String getUsername() {
        return candidato.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
