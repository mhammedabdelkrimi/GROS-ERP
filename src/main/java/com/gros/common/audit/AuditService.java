package com.gros.common.audit;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.gros.modules.auth.entity.Utilisateur;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final JournalAuditRepository repo;

    public void tracer(String action, String entite, Long entiteId) {
        JournalAudit j = new JournalAudit();
        j.setAction(action);
        j.setEntite(entite);
        j.setEntiteId(entiteId);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Utilisateur u) {
            j.setUtilisateurId(u.getId());
        }
        repo.save(j);
    }
}