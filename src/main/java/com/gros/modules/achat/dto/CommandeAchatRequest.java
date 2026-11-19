package com.gros.modules.achat.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CommandeAchatRequest(@NotNull Long fournisseurId,
                                   @NotEmpty @Valid List<LigneAchatRequest> lignes) {}