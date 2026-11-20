package com.gros.modules.vente.dto;

import com.gros.modules.vente.entity.CommandeClient;
import com.gros.modules.vente.entity.StatutCommande;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CommandeResume(Long id, String numero, Long clientId, StatutCommande statut,
                             BigDecimal montantTtc, LocalDateTime dateCreation) {

    public static CommandeResume de(CommandeClient c) {
        return new CommandeResume(c.getId(), c.getNumero(), c.getClientId(), c.getStatut(),
                c.getMontantTtc(), c.getDateCreation());
    }
}