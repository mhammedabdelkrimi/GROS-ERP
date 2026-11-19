package com.gros.modules.achat.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ReceptionRequest(@NotEmpty @Valid List<LigneReceptionRequest> lignes) {}