package com.gros.modules.achat.dto;

import com.gros.modules.achat.entity.AvoirFournisseur;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AvoirFournisseurResponse(Long id, String numero, String numeroFournisseur, Long factureId,
                                       Long fournisseurId, String motif, LocalDate dateAvoir,
                                       BigDecimal montantHt, BigDecimal montantTva, BigDecimal montantTtc) {
    public static AvoirFournisseurResponse de(AvoirFournisseur a) {
        return new AvoirFournisseurResponse(a.getId(), a.getNumero(), a.getNumeroFournisseur(), a.getFactureId(),
                a.getFournisseurId(), a.getMotif(), a.getDateAvoir(), a.getMontantHt(), a.getMontantTva(),
                a.getMontantTtc());
    }
}