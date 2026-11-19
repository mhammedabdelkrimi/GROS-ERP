package com.gros.modules.paiement.service;

import com.gros.modules.facturation.entity.StatutFacture;
import com.gros.modules.facturation.repository.FactureClientRepository;
import com.gros.modules.paiement.repository.AffectationPaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service @RequiredArgsConstructor
public class SoldeClientService {
    private final FactureClientRepository factures;
    private final AffectationPaiementRepository affectations;

    @Transactional(readOnly = true)
    public BigDecimal calculerSolde(Long clientId) {
        return factures.totalFactures(clientId, StatutFacture.ANNULEE)
                .subtract(affectations.totalAffecte(clientId, StatutFacture.ANNULEE));
    }
}