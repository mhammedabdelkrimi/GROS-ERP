package com.gros.modules.paiement.dto;

import com.gros.modules.paiement.entity.ModePaiement;
import com.gros.modules.paiement.entity.Remboursement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RemboursementResponse(Long id, Long clientId, Long factureId, BigDecimal montant,
                                    ModePaiement modePaiement, LocalDateTime dateRemboursement) {
    public static RemboursementResponse de(Remboursement r) {
        return new RemboursementResponse(r.getId(), r.getClientId(), r.getFactureId(), r.getMontant(),
                r.getModePaiement(), r.getDateRemboursement());
    }
}