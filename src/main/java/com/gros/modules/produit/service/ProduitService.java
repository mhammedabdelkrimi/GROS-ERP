package com.gros.modules.produit.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gros.common.SecurityUtils;
import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.modules.produit.dto.HistoriquePrixResponse;
import com.gros.modules.produit.dto.ProduitRequest;
import com.gros.modules.produit.dto.ProduitResponse;
import com.gros.modules.produit.entity.HistoriquePrix;
import com.gros.modules.produit.entity.Produit;
import com.gros.modules.produit.mapper.ProduitMapper;
import com.gros.modules.produit.repository.HistoriquePrixRepository;
import com.gros.modules.produit.repository.ProduitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProduitService {

    private final ProduitRepository repo;
    private final HistoriquePrixRepository histoRepo;
    private final ProduitMapper mapper;
    private final AuditService audit;

    @Transactional(readOnly = true)
    public Page<ProduitResponse> lister(String q, Pageable pageable) {
        return repo.findByActifTrueAndDesignationContainingIgnoreCase(q == null ? "" : q, pageable)
                .map(mapper::toResponse);
    }

    @Transactional
    public ProduitResponse creer(ProduitRequest r) {
        if (repo.existsByReference(r.reference())) {
            throw new BusinessException("Référence déjà utilisée : " + r.reference());
        }
        Produit p = repo.save(mapper.toEntity(r));
        audit.tracer("CREATION", "PRODUIT", p.getId());
        return mapper.toResponse(p);
    }

    @Transactional
    public ProduitResponse modifier(Long id, ProduitRequest r) {
        Produit p = trouver(id);
        BigDecimal ancien = p.getPrixVente();
        mapper.maj(r, p);
        if (ancien.compareTo(p.getPrixVente()) != 0) {          // BNF-19 : prix historisés
            histoRepo.save(new HistoriquePrix(id, ancien, p.getPrixVente(), SecurityUtils.idCourant()));
        }
        audit.tracer("MODIFICATION", "PRODUIT", id);
        return mapper.toResponse(p);
    }

    @Transactional
    public void desactiver(Long id) {
        trouver(id).desactiver();
        audit.tracer("DESACTIVATION", "PRODUIT", id);
    }

    @Transactional(readOnly = true)
    public List<HistoriquePrixResponse> historique(Long id) {
        return histoRepo.findByProduitIdOrderByDateChangementDesc(id).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public Produit trouver(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Produit introuvable : " + id));
    }
}