package com.gros.modules.stock.service;

import com.gros.common.SecurityUtils;
import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.common.numerotation.NumerotationService;
import com.gros.modules.produit.entity.Produit;
import com.gros.modules.produit.repository.ProduitRepository;
import com.gros.modules.stock.dto.ComptageRequest;
import com.gros.modules.stock.dto.InventaireResponse;
import com.gros.modules.stock.dto.InventaireResume;
import com.gros.modules.stock.dto.LigneInventaireResponse;
import com.gros.modules.stock.dto.OuvertureInventaireRequest;
import com.gros.modules.stock.entity.Inventaire;
import com.gros.modules.stock.entity.LigneInventaire;
import com.gros.modules.stock.repository.InventaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class InventaireService {
    private final InventaireRepository inventaires;
    private final ProduitRepository produits;
    private final StockService stock;
    private final NumerotationService numerotation;
    private final AuditService audit;

    @Transactional
    public InventaireResponse ouvrir(OuvertureInventaireRequest r) {
        if (inventaires.existsByClotureFalse())
            throw new BusinessException("Un inventaire est déjà ouvert : clôturez-le d'abord");

        List<Produit> cibles;
        if (r == null || r.produitIds() == null || r.produitIds().isEmpty()) {
            cibles = produits.findByActifTrue();
        } else {
            List<Long> ids = r.produitIds().stream().distinct().toList();
            cibles = produits.findAllById(ids).stream().filter(Produit::isActif).toList();
            if (cibles.size() != ids.size())
                throw new NotFoundException("Un ou plusieurs produits sont introuvables ou désactivés");
        }
        if (cibles.isEmpty()) throw new BusinessException("Aucun produit à inventorier");

        Inventaire inv = new Inventaire();
        inv.setNumero(numerotation.prochainNumero("INV"));
        for (Produit p : cibles)
            inv.ajouterLigne(p.getId(), Math.toIntExact(stock.quantiteDisponible(p.getId())));
        inventaires.save(inv);
        audit.tracer("CREATION", "INVENTAIRE", inv.getId());
        return versReponse(inv);
    }

    @Transactional
    public InventaireResponse compter(Long id, ComptageRequest r) {
        Inventaire inv = trouverAvecVerrou(id);
        inv.verifierOuvert();
        for (var l : r.lignes())
            inv.ligne(l.produitId()).compter(l.quantiteComptee(), l.justification());
        audit.tracer("COMPTAGE", "INVENTAIRE", id);
        return versReponse(inv);
    }

    /** Clôture : un mouvement CORRECTION_INVENTAIRE par écart. Tout réussit ou rien n'est enregistré. */
    @Transactional
    public InventaireResponse cloturer(Long id) {
        Inventaire inv = trouverAvecVerrou(id);
        inv.verifierOuvert();
        inv.verifierPretACloturer();
        for (LigneInventaire l : inv.getLignes())
            stock.corrigerInventaire(l.getProduitId(), l.ecart(), inv.getNumero(), "INVENTAIRE", inv.getId());
        inv.cloturer(SecurityUtils.idCourant());
        audit.tracer("CLOTURE", "INVENTAIRE", id);
        return versReponse(inv);
    }

    @Transactional(readOnly = true)
    public InventaireResponse consulter(Long id) {
        return versReponse(inventaires.findById(id)
                .orElseThrow(() -> new NotFoundException("Inventaire introuvable : " + id)));
    }

    @Transactional(readOnly = true)
    public Page<InventaireResume> lister(Pageable pageable) {
        return inventaires.findAll(pageable).map(i ->
                new InventaireResume(i.getId(), i.getNumero(), i.getDateInventaire(), i.isCloture(), i.getLignes().size()));
    }

    private Inventaire trouverAvecVerrou(Long id) {
        return inventaires.findByIdAvecVerrou(id)
                .orElseThrow(() -> new NotFoundException("Inventaire introuvable : " + id));
    }

    private InventaireResponse versReponse(Inventaire inv) {
        List<Long> ids = inv.getLignes().stream().map(LigneInventaire::getProduitId).toList();
        Map<Long, Produit> parId = produits.findAllById(ids).stream()
                .collect(Collectors.toMap(Produit::getId, Function.identity()));
        List<LigneInventaireResponse> lignes = inv.getLignes().stream().map(l -> {
            Produit p = parId.get(l.getProduitId());
            return new LigneInventaireResponse(l.getProduitId(), p.getReference(), p.getDesignation(),
                    l.getQuantiteTheorique(), l.getQuantiteComptee(), l.ecart(), l.getJustification());
        }).toList();
        return new InventaireResponse(inv.getId(), inv.getNumero(), inv.getDateInventaire(),
                inv.isCloture(), inv.getDateCloture(), lignes);
    }
}