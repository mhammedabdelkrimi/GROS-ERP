package com.gros.modules.vente.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LigneDevisRequest(@NotNull Long produitId, @Positive int quantite) {}