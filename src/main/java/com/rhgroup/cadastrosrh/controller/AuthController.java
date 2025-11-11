package com.rhgroup.cadastrosrh.controller;

import com.rhgroup.cadastrosrh.model.Candidato;
import com.rhgroup.cadastrosrh.model.StatusCandidato;
import com.rhgroup.cadastrosrh.repository.CandidatoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final CandidatoRepository repository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthController(CandidatoRepository repository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public record RegisterRequest(String nome, String email, String senha) {}
    public record LoginRequest(String email, String senha) {}

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (repository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Email já cadastrado!");
        }

        Candidato candidato = Candidato.builder()
                .nome(request.nome())
                .email(request.email())
                .cpf("00000000000")
                .senhaHash(passwordEncoder.encode(request.senha()))
                .dataNascimento(LocalDate.of(1999, 1, 1))
                .areaInteresse("Backend")
                .experienciaAnos(0)
                .pretensaoSalarial(BigDecimal.ZERO)
                .status(StatusCandidato.ATIVO)
                .build();

        repository.save(candidato);
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.senha());
        authenticationManager.authenticate(authToken);
        return ResponseEntity.ok("Login bem-sucedido!");
    }
}
