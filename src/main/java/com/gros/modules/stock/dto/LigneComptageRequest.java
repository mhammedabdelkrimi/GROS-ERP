package com.gros.modules.stock.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record LigneComptageRequest(@NotNull Long produitId,
                                   @NotNull @PositiveOrZero Integer quantiteComptee,
                                   @Size(max = 250) String justification) {}