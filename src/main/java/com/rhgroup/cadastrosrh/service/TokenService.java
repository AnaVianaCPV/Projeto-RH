package com.rhgroup.cadastrosrh.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class TokenService {

    private final JwtEncoder encoder;

    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public TokenHolder generateToken(Authentication auth) {
        Instant now = Instant.now();
        long expiresIn = 3600; // 1h

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("cadastrosrh")
                .issuedAt(now)
                .expiresAt(now.plus(expiresIn, ChronoUnit.SECONDS))
                .subject(auth.getName()) // email
                .claim("scope", "ROLE_USER")
                .build();

        JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();

        String tokenValue = encoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

        return new TokenHolder(tokenValue, "Bearer", expiresIn);
    }

    public record TokenHolder(String token, String tokenType, long expiresIn) {}
}
