package com.gros.modules.achat.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** delaiPaiementJours : 30 si absent. L'échéance = dateFacture + délai. */
public record FactureFournisseurRequest(@NotNull Long commandeId,
                                        @NotBlank @Size(max = 50) String numeroFournisseur,
                                        @NotNull @PastOrPresent LocalDate dateFacture,
                                        @NotNull @Positive BigDecimal montantHt,
                                        @NotNull @PositiveOrZero BigDecimal montantTva,
                                        @Min(0) @Max(365) Integer delaiPaiementJours) {}