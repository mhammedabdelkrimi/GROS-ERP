package com.gros.modules.paiement.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.modules.client.repository.ClientRepository;
import com.gros.modules.comptabilite.service.ComptabiliteService;
import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.facturation.entity.StatutFacture;
import com.gros.modules.facturation.repository.FactureClientRepository;
import com.gros.modules.paiement.dto.AffectationRequest;
import com.gros.modules.paiement.dto.PaiementRequest;
import com.gros.modules.paiement.dto.PaiementResponse;
import com.gros.modules.paiement.entity.AffectationPaiement;
import com.gros.modules.paiement.entity.Paiement;
import com.gros.modules.paiement.repository.AffectationPaiementRepository;
import com.gros.modules.paiement.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service @RequiredArgsConstructor
public class PaiementService {
    private final PaiementRepository paiements;
    private final AffectationPaiementRepository affectations;
    private final FactureClientRepository factures;
    private final ClientRepository clients;
    private final SoldeClientService soldeClient;
    private final ComptabiliteService comptabilite;
    private final AuditService audit;

    @Transactional
    public PaiementResponse enregistrer(PaiementRequest r) {
        if (!clients.existsById(r.clientId()))
            throw new NotFoundException("Client introuvable : " + r.clientId());
        BigDecimal total = r.affectations().stream().map(AffectationRequest::montant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(r.montant()) > 0)
            throw new BusinessException("Le total affecté dépasse le montant du paiement");
        Paiement p = paiements.save(new Paiement(r.clientId(), r.montant(), r.modePaiement()));
        for (var a : r.affectations()) {
            FactureClient f = factures.findById(a.factureId())
                    .orElseThrow(() -> new NotFoundException("Facture introuvable : " + a.factureId()));
            if (!f.getClientId().equals(r.clientId()))
                throw new BusinessException("Cette facture appartient à un autre client");
            if (f.getStatut() == StatutFacture.ANNULEE)
                throw new BusinessException("Facture annulée : paiement impossible");
            f.appliquerPaiement(a.montant());          // PARTIELLEMENT_PAYEE ou PAYEE
            affectations.save(new AffectationPaiement(p.getId(), f.getId(), a.montant()));
        }
        comptabilite.comptabiliserPaiement(p);
        audit.tracer("PAIEMENT", "PAIEMENT", p.getId());
        affectations.flush();
        return PaiementResponse.de(p, soldeClient.calculerSolde(r.clientId()));
    }
}