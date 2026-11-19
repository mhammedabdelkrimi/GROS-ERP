package com.gros.modules.paiement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AffectationRequest(@NotNull Long factureId, @NotNull @Positive BigDecimal montant) {}