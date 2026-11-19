package com.gros.modules.livraison.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefusLivraisonRequest(@NotBlank @Size(max = 250) String motif) {}