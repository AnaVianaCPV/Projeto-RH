package com.RHgroup.CadastrosRH.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Configura a cadeia de filtros de segurança (autorização, autenticação, etc.).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF (Comum para APIs stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // Define política de sessão como Stateless (API REST)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Regras de Autorização
                .authorizeHttpRequests(auth -> auth
                        // Permite acesso irrestrito a endpoints de cadastro/login
                        // A rota de cadastro de candidato deve ser pública para novos usuários
                        .requestMatchers("/api/candidatos").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        // Exige autenticação para qualquer outra requisição
                        .anyRequest().authenticated()
                )
                // Usa autenticação HTTP Básica para este exemplo (requer login/senha em cada requisição)
                .httpBasic(httpBasic -> {}); // Configura a autenticação básica

        return http.build();
    }

    /**
     * Define o codificador de senha (BCrypt é o padrão e mais seguro).
     * ESSENCIAL: As senhas no banco de dados DEVEM ser armazenadas usando este codificador.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Expõe o AuthenticationManager para ser injetado em outros serviços (ex: serviço de Login/Token).
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}