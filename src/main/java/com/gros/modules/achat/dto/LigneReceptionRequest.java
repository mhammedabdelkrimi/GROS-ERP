package com.gros.modules.achat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record LigneReceptionRequest(@NotNull Long produitId, @Positive int quantiteRecue,
                                    @NotBlank String numeroLot, LocalDate datePeremption) {}