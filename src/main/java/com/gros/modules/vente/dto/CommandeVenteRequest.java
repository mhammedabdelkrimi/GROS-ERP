package com.gros.modules.vente.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CommandeVenteRequest(@NotNull Long clientId, @NotEmpty @Valid List<LigneVenteRequest> lignes) {}