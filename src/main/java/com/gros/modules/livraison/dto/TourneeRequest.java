package com.gros.modules.livraison.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record TourneeRequest(@NotNull LocalDate dateTournee, @NotNull Long livreurId, List<Long> bonIds) {}