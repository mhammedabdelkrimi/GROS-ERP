package com.gros.modules.livraison.dto;

import com.gros.modules.livraison.entity.BonLivraison;
import com.gros.modules.livraison.entity.StatutBon;

public record BonLivraisonResponse(Long id, String numero, Long commandeId, StatutBon statut,
                                   boolean signeParClient, String motifRefus) {

    public static BonLivraisonResponse de(BonLivraison b) {
        return new BonLivraisonResponse(b.getId(), b.getNumero(), b.getCommandeId(), b.getStatut(),
                b.isSigneParClient(), b.getMotifRefus());
    }
}