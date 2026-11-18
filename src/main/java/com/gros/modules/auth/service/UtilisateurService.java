package com.gros.modules.auth.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.modules.auth.dto.UtilisateurRequest;
import com.gros.modules.auth.dto.UtilisateurResponse;
import com.gros.modules.auth.entity.Utilisateur;
import com.gros.modules.auth.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class UtilisateurService {
    private final UtilisateurRepository repo;
    private final PasswordEncoder encoder;
    private final AuditService audit;

    @Transactional
    public UtilisateurResponse creer(UtilisateurRequest r) {
        if (repo.findByEmail(r.email()).isPresent()) throw new BusinessException("Email déjà utilisé");
        Utilisateur u = new Utilisateur();
        u.setNom(r.nom()); u.setEmail(r.email()); u.setRole(r.role());
        u.setMotDePasseHache(encoder.encode(r.motDePasse()));
        repo.save(u);
        audit.tracer("CREATION", "UTILISATEUR", u.getId());
        return new UtilisateurResponse(u.getId(), u.getNom(), u.getEmail(), u.getRole(), u.isActif());
    }

    @Transactional
    public void desactiver(Long id) {
        Utilisateur u = repo.findById(id).orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
        u.setActif(false);
        audit.tracer("DESACTIVATION", "UTILISATEUR", id);
    }
}