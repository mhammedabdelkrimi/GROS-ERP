package com.gros.modules.achat.dto;

import com.gros.modules.achat.entity.CommandeFournisseur;
import com.gros.modules.achat.entity.StatutCommandeAchat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AchatResume(Long id, String numero, Long fournisseurId, StatutCommandeAchat statut,
                          BigDecimal montantTotal, LocalDateTime dateCreation) {

    public static AchatResume de(CommandeFournisseur c) {
        return new AchatResume(c.getId(), c.getNumero(), c.getFournisseurId(), c.getStatut(),
                c.getMontantTotal(), c.getDateCreation());
    }
}