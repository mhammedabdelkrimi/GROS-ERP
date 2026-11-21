package com.gros.modules.vente.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DevisRequest(@NotNull Long clientId,
                           @Min(1) @Max(365) Integer validiteJours,
                           @NotEmpty @Valid List<LigneDevisRequest> lignes) {}