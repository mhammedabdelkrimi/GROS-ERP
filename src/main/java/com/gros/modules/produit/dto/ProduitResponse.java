package com.gros.modules.produit.dto;

import java.math.BigDecimal;

public record ProduitResponse(
        Long id,
        String reference,
        String designation,
        Long categorieId,
        String unite,
        BigDecimal prixAchat,
        BigDecimal prixVente,
        BigDecimal tauxTva,
        int seuilAlerte,
        boolean perissable,
        boolean actif) {

    public ProduitResponse sansPrixAchat() {
        return new ProduitResponse(id, reference, designation, categorieId, unite,
                null, prixVente, tauxTva, seuilAlerte, perissable, actif);
    }
}