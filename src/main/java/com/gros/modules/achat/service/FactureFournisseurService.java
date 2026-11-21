package com.gros.modules.achat.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.common.numerotation.NumerotationService;
import com.gros.modules.achat.dto.FactureFournisseurRequest;
import com.gros.modules.achat.dto.FactureFournisseurResponse;
import com.gros.modules.achat.dto.PaiementFournisseurRequest;
import com.gros.modules.achat.entity.CommandeFournisseur;
import com.gros.modules.achat.entity.FactureFournisseur;
import com.gros.modules.achat.entity.PaiementFournisseur;
import com.gros.modules.achat.entity.StatutCommandeAchat;
import com.gros.modules.achat.entity.StatutFactureFournisseur;
import com.gros.modules.achat.repository.CommandeFournisseurVerrouRepository;
import com.gros.modules.achat.repository.FactureFournisseurRepository;
import com.gros.modules.achat.repository.PaiementFournisseurRepository;
import com.gros.modules.comptabilite.service.ComptabiliteService;
import com.gros.modules.produit.entity.Produit;
import com.gros.modules.produit.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service @RequiredArgsConstructor
public class FactureFournisseurService {
    private static final BigDecimal CENT = BigDecimal.valueOf(100);

    private final FactureFournisseurRepository factures;
    private final PaiementFournisseurRepository paiements;
    private final CommandeFournisseurVerrouRepository commandes;
    private final ProduitRepository produits;
    private final ComptabiliteService comptabilite;
    private final NumerotationService numerotation;
    private final AuditService audit;

    /** Rapprochement : on ne facture que ce qui a été réceptionné (section 2.2). */
    @Transactional
    public FactureFournisseurResponse creer(FactureFournisseurRequest r) {
        CommandeFournisseur c = commandes.findByIdAvecVerrou(r.commandeId())
                .orElseThrow(() -> new NotFoundException("Commande introuvable : " + r.commandeId()));
        if (c.getStatut() != StatutCommandeAchat.PARTIELLEMENT_RECUE && c.getStatut() != StatutCommandeAchat.RECUE)
            throw new BusinessException("Aucune marchandise réceptionnée pour cette commande : facturation impossible");

        String numeroFournisseur = r.numeroFournisseur().trim();
        if (factures.existsByFournisseurIdAndNumeroFournisseur(c.getFournisseurId(), numeroFournisseur))
            throw new BusinessException("Facture déjà enregistrée pour ce fournisseur : " + numeroFournisseur);

        BigDecimal recu = c.getLignes().stream()
                .map(l -> l.getPrixUnitaire().multiply(BigDecimal.valueOf(l.getQuantiteRecue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal reste = recu.subtract(factures.totalHtParCommande(c.getId()));
        BigDecimal ht = r.montantHt().setScale(2, RoundingMode.HALF_UP);
        if (ht.compareTo(reste) > 0)
            throw new BusinessException("Montant HT supérieur à ce qui reste à facturer : " + reste
                    + " (valeur réceptionnée " + recu + ")");

        List<Long> produitIds = c.getLignes().stream().map(l -> l.getProduitId()).toList();
        BigDecimal tauxMax = produits.findAllById(produitIds).stream()
                .map(Produit::getTauxTva).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        BigDecimal tvaMax = ht.multiply(tauxMax).divide(CENT, 2, RoundingMode.HALF_UP);
        BigDecimal tva = r.montantTva().setScale(2, RoundingMode.HALF_UP);
        if (tva.compareTo(tvaMax) > 0)
            throw new BusinessException("TVA supérieure au maximum attendu : " + tvaMax);

        FactureFournisseur f = new FactureFournisseur();
        f.setNumero(numerotation.prochainNumero("FF"));
        f.setNumeroFournisseur(numeroFournisseur);
        f.setCommandeId(c.getId());
        f.setFournisseurId(c.getFournisseurId());
        f.setDateFacture(r.dateFacture());
        f.setDateEcheance(r.dateFacture().plusDays(r.delaiPaiementJours() == null ? 30 : r.delaiPaiementJours()));
        f.setMontantHt(ht);
        f.setMontantTva(tva);
        f.setMontantTtc(ht.add(tva));
        factures.save(f);
        comptabilite.comptabiliserFactureFournisseur(f.getNumero(), f.getId(), ht, tva, f.getMontantTtc());
        audit.tracer("CREATION", "FACTURE_FOURNISSEUR", f.getId());
        return FactureFournisseurResponse.de(f);
    }

    @Transactional
    public FactureFournisseurResponse payer(Long factureId, PaiementFournisseurRequest r) {
        FactureFournisseur f = factures.findByIdAvecVerrou(factureId)
                .orElseThrow(() -> new NotFoundException("Facture fournisseur introuvable : " + factureId));
        f.appliquerPaiement(r.montant());       // refuse si le montant dépasse le reste à payer
        PaiementFournisseur p = paiements.save(new PaiementFournisseur(f.getId(), r.montant(), r.modePaiement()));
        comptabilite.comptabiliserPaiementFournisseur(p.getId(), f.getId(), r.modePaiement(), r.montant());
        audit.tracer("PAIEMENT", "PAIEMENT_FOURNISSEUR", p.getId());
        return FactureFournisseurResponse.de(f);
    }

    @Transactional(readOnly = true)
    public FactureFournisseurResponse consulter(Long id) {
        return factures.findById(id).map(FactureFournisseurResponse::de)
                .orElseThrow(() -> new NotFoundException("Facture fournisseur introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public Page<FactureFournisseurResponse> lister(StatutFactureFournisseur statut, Pageable pageable) {
        var page = (statut == null) ? factures.findAll(pageable) : factures.findByStatut(statut, pageable);
        return page.map(FactureFournisseurResponse::de);
    }
}