package com.gros.modules.pilotage.dto;

import java.math.BigDecimal;

public record ProduitPerformanceResponse(Long produitId, String reference, String designation,
                                         long quantiteVendue, BigDecimal caHt, BigDecimal cout,
                                         BigDecimal marge, BigDecimal tauxMarge) {}