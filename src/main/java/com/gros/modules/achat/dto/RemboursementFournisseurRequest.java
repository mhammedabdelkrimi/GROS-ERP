package com.gros.modules.achat.dto;

import com.gros.modules.paiement.entity.ModePaiement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RemboursementFournisseurRequest(@NotNull @Positive BigDecimal montant,
                                              @NotNull ModePaiement modePaiement) {}