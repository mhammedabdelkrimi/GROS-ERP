package com.gros.modules.paiement.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.NotFoundException;
import com.gros.modules.client.repository.ClientRepository;
import com.gros.modules.comptabilite.service.ComptabiliteService;
import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.facturation.entity.StatutFacture;
import com.gros.modules.facturation.repository.FactureClientRepository;
import com.gros.modules.paiement.dto.CreditClientResponse;
import com.gros.modules.paiement.dto.CreditFactureResponse;
import com.gros.modules.paiement.dto.RemboursementRequest;
import com.gros.modules.paiement.dto.RemboursementResponse;
import com.gros.modules.paiement.entity.Remboursement;
import com.gros.modules.paiement.repository.RemboursementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service @RequiredArgsConstructor
public class RemboursementService {
    private final RemboursementRepository remboursements;
    private final FactureClientRepository factures;
    private final ClientRepository clients;
    private final SoldeClientService solde;
    private final ComptabiliteService comptabilite;
    private final AuditService audit;

    @Transactional
    public RemboursementResponse enregistrer(RemboursementRequest r) {
        FactureClient f = factures.findByIdAvecVerrou(r.factureId())
                .orElseThrow(() -> new NotFoundException("Facture introuvable : " + r.factureId()));
        f.rembourser(r.montant());   // refuse si le montant dépasse le crédit de CETTE facture
        Remboursement rb = remboursements.save(
                new Remboursement(f.getClientId(), f.getId(), r.montant(), r.modePaiement()));
        comptabilite.comptabiliserRemboursement(rb);
        audit.tracer("REMBOURSEMENT", "REMBOURSEMENT", rb.getId());
        return RemboursementResponse.de(rb);
    }

    @Transactional(readOnly = true)
    public CreditClientResponse credit(Long clientId) {
        clients.findById(clientId).orElseThrow(() -> new NotFoundException("Client introuvable : " + clientId));
        List<CreditFactureResponse> detail = factures
                .findByClientIdAndStatutNotOrderByIdAsc(clientId, StatutFacture.ANNULEE).stream()
                .filter(f -> f.creditGenere().signum() > 0)
                .map(f -> new CreditFactureResponse(f.getId(), f.getNumero(), f.creditGenere()))
                .toList();
        BigDecimal total = detail.stream().map(CreditFactureResponse::credit).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CreditClientResponse(clientId, total, solde.calculerSolde(clientId), detail);
    }
}