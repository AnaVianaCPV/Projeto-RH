package com.rhgroup.cadastrosrh.controller;

import com.rhgroup.cadastrosrh.security.dto.LoginRequest;
import com.rhgroup.cadastrosrh.security.dto.TokenResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api/v1/candidatos", produces= MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")

public class AuthController {

    private final AuthenticationManager authManager;
    private final com.rhgroup.cadastrosrh.service.TokenService tokenService;

    public AuthController(AuthenticationManager authManager, com.rhgroup.cadastrosrh.service.TokenService tokenService) {
        this.authManager = authManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        var token = tokenService.generateToken(authentication);
        return ResponseEntity.ok(new TokenResponse(token.token(), token.tokenType(), token.expiresIn()));
    }
}
