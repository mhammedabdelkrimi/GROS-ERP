package com.gros.modules.achat.dto;

import com.gros.modules.achat.entity.CommandeFournisseur;
import com.gros.modules.achat.entity.StatutCommandeAchat;

import java.math.BigDecimal;
import java.util.List;

public record CommandeAchatResponse(Long id, String numero, Long fournisseurId, StatutCommandeAchat statut,
                                    BigDecimal montantTotal, List<LigneAchatResponse> lignes) {

    public static CommandeAchatResponse de(CommandeFournisseur c) {
        return new CommandeAchatResponse(c.getId(), c.getNumero(), c.getFournisseurId(), c.getStatut(),
                c.getMontantTotal(),
                c.getLignes().stream()
                        .map(l -> new LigneAchatResponse(l.getProduitId(), l.getQuantite(), l.getQuantiteRecue(), l.getPrixUnitaire()))
                        .toList());
    }
}