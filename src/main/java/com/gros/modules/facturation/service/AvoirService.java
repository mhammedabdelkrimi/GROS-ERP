package com.gros.modules.facturation.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.common.numerotation.NumerotationService;
import com.gros.modules.comptabilite.service.ComptabiliteService;
import com.gros.modules.facturation.dto.AvoirRequest;
import com.gros.modules.facturation.dto.AvoirResponse;
import com.gros.modules.facturation.entity.Avoir;
import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.facturation.entity.StatutFacture;
import com.gros.modules.facturation.repository.AvoirRepository;
import com.gros.modules.facturation.repository.FactureClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class AvoirService {
    private final AvoirRepository avoirs;
    private final FactureClientRepository factures;
    private final NumerotationService numerotation;
    private final ComptabiliteService comptabilite;
    private final AuditService audit;

    @Transactional
    public AvoirResponse creer(AvoirRequest r) {
        FactureClient f = factures.findByIdAvecVerrou(r.factureId())
                .orElseThrow(() -> new NotFoundException("Facture introuvable : " + r.factureId()));
        if (f.getStatut() == StatutFacture.ANNULEE)
            throw new BusinessException("Facture annulée : avoir impossible");
        f.appliquerAvoir(r.montant());   // refuse si le montant dépasse le net facturé
        Avoir a = avoirs.save(new Avoir(numerotation.prochainNumero("AV"), f.getId(), r.motif(), r.montant()));
        comptabilite.comptabiliserAvoir(a, f);
        audit.tracer("CREATION", "AVOIR", a.getId());
        return AvoirResponse.de(a);
    }

    @Transactional(readOnly = true)
    public Page<AvoirResponse> lister(Pageable pageable) {
        return avoirs.findAll(pageable).map(AvoirResponse::de);
    }
}