package com.gros.modules.fournisseur.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.NotFoundException;
import com.gros.modules.fournisseur.dto.FournisseurRequest;
import com.gros.modules.fournisseur.dto.FournisseurResponse;
import com.gros.modules.fournisseur.entity.Fournisseur;
import com.gros.modules.fournisseur.mapper.FournisseurMapper;
import com.gros.modules.fournisseur.repository.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class FournisseurService {
    private final FournisseurRepository repo;
    private final FournisseurMapper mapper;
    private final AuditService audit;

    @Transactional(readOnly = true)
    public Page<FournisseurResponse> lister(String q, Pageable pageable) {
        return repo.findByActifTrueAndRaisonSocialeContainingIgnoreCase(q == null ? "" : q, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public FournisseurResponse creer(FournisseurRequest r) {
        Fournisseur f = repo.save(mapper.toEntity(r));
        audit.tracer("CREATION", "FOURNISSEUR", f.getId());
        return mapper.toResponse(f);
    }

    @Transactional
    public FournisseurResponse modifier(Long id, FournisseurRequest r) {
        Fournisseur f = trouver(id);
        mapper.maj(r, f);
        audit.tracer("MODIFICATION", "FOURNISSEUR", id);
        return mapper.toResponse(f);
    }

    @Transactional
    public void desactiver(Long id) {
        trouver(id).desactiver();
        audit.tracer("DESACTIVATION", "FOURNISSEUR", id);
    }

    public Fournisseur trouver(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Fournisseur introuvable : " + id));
    }
}