package com.gros.modules.auth.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gros.config.JwtService;
import com.gros.modules.auth.dto.LoginRequest;
import com.gros.modules.auth.dto.LoginResponse;
import com.gros.modules.auth.entity.Utilisateur;
import com.gros.modules.auth.repository.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @Transactional(noRollbackFor = BadCredentialsException.class)
    public LoginResponse login(LoginRequest req) {
        Utilisateur u = repo.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));
        if (!u.isActif()) throw new LockedException("Compte désactivé");
        if (u.estVerrouille()) throw new LockedException("Compte bloqué temporairement après trop d'échecs");
        if (!encoder.matches(req.motDePasse(), u.getMotDePasseHache())) {
            u.enregistrerEchec();
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }
        u.reinitialiserEchecs();
        return new LoginResponse(jwt.generer(u), u.getNom(), u.getRole().name());
    }
}