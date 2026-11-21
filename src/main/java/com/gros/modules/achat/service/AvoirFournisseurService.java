package com.gros.modules.achat.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.common.numerotation.NumerotationService;
import com.gros.modules.achat.dto.AvoirFournisseurRequest;
import com.gros.modules.achat.dto.AvoirFournisseurResponse;
import com.gros.modules.achat.dto.FactureFournisseurResponse;
import com.gros.modules.achat.dto.RemboursementFournisseurRequest;
import com.gros.modules.achat.entity.AvoirFournisseur;
import com.gros.modules.achat.entity.FactureFournisseur;
import com.gros.modules.achat.repository.AvoirFournisseurRepository;
import com.gros.modules.achat.repository.FactureFournisseurRepository;
import com.gros.modules.comptabilite.service.ComptabiliteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service @RequiredArgsConstructor
public class AvoirFournisseurService {
    private final AvoirFournisseurRepository avoirs;
    private final FactureFournisseurRepository factures;
    private final ComptabiliteService comptabilite;
    private final NumerotationService numerotation;
    private final AuditService audit;

    @Transactional
    public AvoirFournisseurResponse creer(AvoirFournisseurRequest r) {
        FactureFournisseur f = factures.findByIdAvecVerrou(r.factureId())
                .orElseThrow(() -> new NotFoundException("Facture fournisseur introuvable : " + r.factureId()));
        String numeroFournisseur = r.numeroFournisseur().trim();
        if (avoirs.existsByFournisseurIdAndNumeroFournisseur(f.getFournisseurId(), numeroFournisseur))
            throw new BusinessException("Avoir déjà enregistré pour ce fournisseur : " + numeroFournisseur);
        BigDecimal ht = r.montantHt().setScale(2, RoundingMode.HALF_UP);
        BigDecimal tva = r.montantTva().setScale(2, RoundingMode.HALF_UP);
        f.appliquerAvoir(ht, tva);            // refuse si l'avoir dépasse ce qui reste de la facture
        AvoirFournisseur a = avoirs.save(new AvoirFournisseur(numerotation.prochainNumero("AF"),
                numeroFournisseur, f.getId(), f.getFournisseurId(), r.motif().trim(), ht, tva, r.dateAvoir()));
        comptabilite.comptabiliserAvoirFournisseur(a.getNumero(), a.getId(), ht, tva, a.getMontantTtc());
        audit.tracer("CREATION", "AVOIR_FOURNISSEUR", a.getId());
        return AvoirFournisseurResponse.de(a);
    }

    /** Le fournisseur rembourse le trop-payé généré par un avoir sur une facture déjà réglée. */
    @Transactional
    public FactureFournisseurResponse rembourser(Long factureId, RemboursementFournisseurRequest r) {
        FactureFournisseur f = factures.findByIdAvecVerrou(factureId)
                .orElseThrow(() -> new NotFoundException("Facture fournisseur introuvable : " + factureId));
        f.rembourser(r.montant());
        comptabilite.comptabiliserRemboursementFournisseur(f.getId(), r.modePaiement(), r.montant());
        audit.tracer("REMBOURSEMENT", "FACTURE_FOURNISSEUR", f.getId());
        return FactureFournisseurResponse.de(f);
    }

    @Transactional(readOnly = true)
    public Page<AvoirFournisseurResponse> lister(Pageable pageable) {
        return avoirs.findAll(pageable).map(AvoirFournisseurResponse::de);
    }
}