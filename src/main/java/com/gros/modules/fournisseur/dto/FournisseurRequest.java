package com.gros.modules.fournisseur.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record FournisseurRequest(
        @NotBlank @Size(max = 150) String raisonSociale,
        @Size(max = 250) String adresse,
        @Size(max = 30) String telephone,
        @Size(max = 100) String conditionsPaiement,
        @Positive int delaiLivraisonJours) {}