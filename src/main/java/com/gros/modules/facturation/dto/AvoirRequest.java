package com.gros.modules.facturation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AvoirRequest(@NotNull Long factureId,
                           @NotBlank @Size(max = 250) String motif,
                           @NotNull @Positive BigDecimal montant) {}