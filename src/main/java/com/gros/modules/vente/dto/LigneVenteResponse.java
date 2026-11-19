package com.gros.modules.vente.dto;

import java.math.BigDecimal;

public record LigneVenteResponse(Long produitId, int quantite, BigDecimal prixUnitaire,
                                 BigDecimal remisePourcent, BigDecimal tauxTva) {}