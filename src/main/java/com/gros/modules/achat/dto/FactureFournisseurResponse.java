package com.gros.modules.achat.dto;

import com.gros.modules.achat.entity.FactureFournisseur;
import com.gros.modules.achat.entity.StatutFactureFournisseur;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FactureFournisseurResponse(Long id, String numero, String numeroFournisseur, Long commandeId,
                                         Long fournisseurId, LocalDate dateFacture, LocalDate dateEcheance,
                                         BigDecimal montantHt, BigDecimal montantTva, BigDecimal montantTtc,
                                         BigDecimal montantAvoirTtc, BigDecimal montantPaye,
                                         BigDecimal montantRembourse, BigDecimal resteAPayer,
                                         BigDecimal creditFournisseur,
                                         StatutFactureFournisseur statut, boolean enRetard) {
    public static FactureFournisseurResponse de(FactureFournisseur f) {
        return new FactureFournisseurResponse(f.getId(), f.getNumero(), f.getNumeroFournisseur(), f.getCommandeId(),
                f.getFournisseurId(), f.getDateFacture(), f.getDateEcheance(), f.getMontantHt(), f.getMontantTva(),
                f.getMontantTtc(), f.getMontantAvoirTtc(), f.getMontantPaye(), f.getMontantRembourse(),
                f.resteAPayer(), f.creditFournisseur(), f.getStatut(), f.estEnRetard());
    }
}