package com.gros.modules.facturation.dto;

import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.facturation.entity.StatutFacture;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FactureResponse(Long id, String numero, Long clientId, LocalDate dateEmission,
                              LocalDate dateEcheance, BigDecimal montantHt, BigDecimal montantTva, BigDecimal montantTtc,
                              BigDecimal montantPaye, BigDecimal montantAvoir, BigDecimal montantRembourse,
                              BigDecimal creditGenere, StatutFacture statut, boolean enRetard) {

    public static FactureResponse de(FactureClient f) {
        return new FactureResponse(f.getId(), f.getNumero(), f.getClientId(), f.getDateEmission(),
                f.getDateEcheance(), f.getMontantHt(), f.getMontantTva(), f.getMontantTtc(),
                f.getMontantPaye(), f.getMontantAvoir(), f.getMontantRembourse(),
                f.creditGenere(), f.getStatut(), f.estEnRetard());
    }
}