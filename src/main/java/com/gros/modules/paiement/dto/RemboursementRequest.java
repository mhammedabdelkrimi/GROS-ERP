package com.gros.modules.paiement.dto;

import com.gros.modules.paiement.entity.ModePaiement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RemboursementRequest(@NotNull Long factureId,
                                   @NotNull @Positive BigDecimal montant,
                                   @NotNull ModePaiement modePaiement) {}