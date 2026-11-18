package com.gros.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gros.modules.auth.entity.Utilisateur;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey cle;
    private final long dureeMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes}") long minutes) {
        this.cle = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.dureeMs = minutes * 60_000;
    }

    public String generer(Utilisateur u) {
        return Jwts.builder()
                .subject(u.getEmail())
                .claim("role", u.getRole().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + dureeMs))
                .signWith(cle)
                .compact();
    }

    public String extraireEmail(String token) {
        return Jwts.parser().verifyWith(cle).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }
}