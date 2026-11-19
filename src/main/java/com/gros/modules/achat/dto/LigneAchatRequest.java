package com.gros.modules.achat.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record LigneAchatRequest(@NotNull Long produitId, @Positive int quantite,
                                @NotNull @PositiveOrZero BigDecimal prixUnitaire) {}