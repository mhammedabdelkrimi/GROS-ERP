package com.gros.modules.paiement.dto;

import com.gros.modules.paiement.entity.ModePaiement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record PaiementRequest(@NotNull Long clientId, @NotNull @Positive BigDecimal montant,
                              @NotNull ModePaiement modePaiement, @NotEmpty @Valid List<AffectationRequest> affectations) {}