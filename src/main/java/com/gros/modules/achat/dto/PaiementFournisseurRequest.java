package com.gros.modules.achat.dto;

import com.gros.modules.paiement.entity.ModePaiement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaiementFournisseurRequest(@NotNull @Positive BigDecimal montant, @NotNull ModePaiement modePaiement) {}