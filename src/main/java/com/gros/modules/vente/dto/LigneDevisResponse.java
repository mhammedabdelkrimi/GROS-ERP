package com.gros.modules.vente.dto;

import java.math.BigDecimal;

public record LigneDevisResponse(Long produitId, String designation, int quantite,
                                 BigDecimal prixUnitaire, BigDecimal tauxTva, BigDecimal montantHt) {}