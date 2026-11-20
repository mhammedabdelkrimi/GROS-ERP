package com.gros.modules.facturation.dto;

import com.gros.modules.facturation.entity.Avoir;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AvoirResponse(Long id, String numero, Long factureId, String motif,
                            BigDecimal montant, LocalDateTime dateCreation) {
    public static AvoirResponse de(Avoir a) {
        return new AvoirResponse(a.getId(), a.getNumero(), a.getFactureId(), a.getMotif(),
                a.getMontant(), a.getDateCreation());
    }
}