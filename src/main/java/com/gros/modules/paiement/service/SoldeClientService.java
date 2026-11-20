package com.gros.modules.paiement.service;

import com.gros.modules.facturation.entity.StatutFacture;
import com.gros.modules.facturation.repository.AvoirRepository;
import com.gros.modules.facturation.repository.FactureClientRepository;
import com.gros.modules.paiement.repository.AffectationPaiementRepository;
import com.gros.modules.paiement.repository.RemboursementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service @RequiredArgsConstructor
public class SoldeClientService {
    private final FactureClientRepository factures;
    private final AffectationPaiementRepository affectations;
    private final AvoirRepository avoirs;
    private final RemboursementRepository remboursements;

    /** Positif = le client doit de l'argent. Négatif = le client a un crédit. */
    @Transactional(readOnly = true)
    public BigDecimal calculerSolde(Long clientId) {
        return factures.totalFactures(clientId, StatutFacture.ANNULEE)
                .subtract(avoirs.totalAvoirs(clientId, StatutFacture.ANNULEE))
                .subtract(affectations.totalAffecte(clientId, StatutFacture.ANNULEE))
                .add(remboursements.totalRembourse(clientId));
    }

    @Transactional(readOnly = true)
    public BigDecimal creditDisponible(Long clientId) {
        return calculerSolde(clientId).negate().max(BigDecimal.ZERO);
    }
}