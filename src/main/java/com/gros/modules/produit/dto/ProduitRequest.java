package com.gros.modules.produit.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProduitRequest(
        @NotBlank @Size(max = 30) String reference,
        @NotBlank String designation,
        Long categorieId,
        @NotBlank String unite,
        @NotNull @PositiveOrZero BigDecimal prixAchat,
        @NotNull @PositiveOrZero BigDecimal prixVente,
        @NotNull @PositiveOrZero BigDecimal tauxTva,
        @PositiveOrZero int seuilAlerte,
        boolean perissable) {
}