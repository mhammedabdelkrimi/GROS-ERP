package com.gros.modules.client.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ClientRequest(
        @NotBlank @Size(max = 150) String raisonSociale,
        @Size(max = 250) String adresse,
        @Size(max = 30) String telephone,
        @NotNull @PositiveOrZero BigDecimal limiteCredit,
        @Positive int delaiPaiementJours,
        @DecimalMin("0") @DecimalMax("100") BigDecimal remiseHabituelle) {}