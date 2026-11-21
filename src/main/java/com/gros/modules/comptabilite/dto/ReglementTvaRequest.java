package com.gros.modules.comptabilite.dto;

import com.gros.modules.paiement.entity.ModePaiement;
import jakarta.validation.constraints.NotNull;

public record ReglementTvaRequest(@NotNull ModePaiement modePaiement) {}