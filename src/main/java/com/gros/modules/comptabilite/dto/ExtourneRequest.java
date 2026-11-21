package com.gros.modules.comptabilite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExtourneRequest(@NotBlank @Size(max = 150) String motif) {}