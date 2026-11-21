package com.gros.modules.achat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AvoirFournisseurRequest(@NotNull Long factureId,
                                      @NotBlank @Size(max = 50) String numeroFournisseur,
                                      @NotNull @PastOrPresent LocalDate dateAvoir,
                                      @NotBlank @Size(max = 250) String motif,
                                      @NotNull @Positive BigDecimal montantHt,
                                      @NotNull @PositiveOrZero BigDecimal montantTva) {}