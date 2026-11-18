package com.gros.modules.produit.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HistoriquePrixResponse(
        BigDecimal ancienPrix,
        BigDecimal nouveauPrix,
        LocalDateTime dateChangement,
        Long modifiePar) {
}