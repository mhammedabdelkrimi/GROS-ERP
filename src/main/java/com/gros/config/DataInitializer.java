package com.gros.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.gros.modules.auth.entity.Role;
import com.gros.modules.auth.entity.Utilisateur;
import com.gros.modules.auth.repository.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository repo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            Utilisateur a = new Utilisateur();
            a.setNom("Administrateur");
            a.setEmail("admin@gros.ma");
            a.setMotDePasseHache(encoder.encode("Admin123!"));
            a.setRole(Role.ADMINISTRATEUR);
            repo.save(a);
        }
    }
}