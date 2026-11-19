package com.gros.modules.paiement.dto;

import com.gros.modules.paiement.entity.ModePaiement;
import com.gros.modules.paiement.entity.Paiement;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaiementResponse(Long id, Long clientId, BigDecimal montant, ModePaiement modePaiement,
                               LocalDate dateReglement, BigDecimal soldeClient) {

    public static PaiementResponse de(Paiement p, BigDecimal soldeClient) {
        return new PaiementResponse(p.getId(), p.getClientId(), p.getMontant(), p.getModePaiement(),
                p.getDateReglement(), soldeClient);
    }
}