package com.gros.modules.vente.dto;

import com.gros.modules.vente.entity.CommandeClient;
import com.gros.modules.vente.entity.StatutCommande;

import java.math.BigDecimal;
import java.util.List;

public record CommandeResponse(Long id, String numero, Long clientId, StatutCommande statut,
                               BigDecimal montantHt, BigDecimal montantTva, BigDecimal montantTtc, List<LigneVenteResponse> lignes) {

    public static CommandeResponse de(CommandeClient c) {
        return new CommandeResponse(c.getId(), c.getNumero(), c.getClientId(), c.getStatut(),
                c.getMontantHt(), c.getMontantTva(), c.getMontantTtc(),
                c.getLignes().stream()
                        .map(l -> new LigneVenteResponse(l.getProduitId(), l.getQuantite(), l.getPrixUnitaire(),
                                l.getRemisePourcent(), l.getTauxTva()))
                        .toList());
    }
}