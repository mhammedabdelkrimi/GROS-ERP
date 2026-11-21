package com.gros.config;

import com.gros.modules.auth.entity.Role;
import com.gros.modules.auth.entity.Utilisateur;
import com.gros.modules.auth.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UtilisateurRepository repo;
    private final PasswordEncoder encoder;

    @Value("${app.admin.email}") private String email;
    @Value("${app.admin.password}") private String motDePasse;

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        if (motDePasse == null || motDePasse.length() < 8)
            throw new IllegalStateException("ADMIN_PASSWORD doit contenir au moins 8 caractères");
        Utilisateur a = new Utilisateur();
        a.setNom("Administrateur");
        a.setEmail(email);
        a.setMotDePasseHache(encoder.encode(motDePasse));
        a.setRole(Role.ADMINISTRATEUR);
        repo.save(a);
    }
}