package com.RHgroup.CadastrosRH.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // Importante

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name= "candidatos")

// Implementa UserDetails
public class Candidato implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "cpf", length = 11, nullable = false, unique = true)
    private String cpf;

    private LocalDate dataNascimento;

    @Column(nullable = false, unique = true)
    private String email;

    // Novo campo para a senha, NUNCA nullable=false, pois usuários antigos podem não ter
    // Se for um sistema novo, deve ser nullable = false. Para este exemplo, vamos assumir que pode ser nulo se não for um usuário do sistema.
    private String senha;

    private String celular;

    @Column(name = "area_interesse")
    private String areaInteresse;

    @Column(name = "experiencia_anos", nullable = false)
    private Integer experienciaAnos;

    private BigDecimal pretensaoSalarial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCandidato status; // Tipo correto: StatusCandidato


    // Campos de auditoria
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void aoCriar() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void aoAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }

    // ----------------------------------------------------------------------
    // Implementação de UserDetails
    // ----------------------------------------------------------------------

    /**
     * Define as permissões (roles) do usuário.
     * Estamos definindo uma role fixa 'ROLE_USER' para todos os candidatos.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Você pode retornar roles mais específicas baseadas em um campo 'role' no Candidato
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.senha; // Retorna o campo 'senha'
    }

    @Override
    public String getUsername() {
        return this.email; // Usamos o email como nome de usuário
    }

    // Métodos de expiração/bloqueio (geralmente retornam true por padrão)
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
        // Apenas permite login se o status não for REPROVADO, por exemplo
        return this.status != StatusCandidato.REPROVADO;
    }
}