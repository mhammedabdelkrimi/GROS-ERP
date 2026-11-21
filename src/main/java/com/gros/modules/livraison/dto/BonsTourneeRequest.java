package com.gros.modules.livraison.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BonsTourneeRequest(@NotEmpty List<Long> bonIds) {}