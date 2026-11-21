package com.gros.modules.stock.dto;

import java.time.LocalDate;

public record InventaireResume(Long id, String numero, LocalDate dateInventaire, boolean cloture, int nombreLignes) {}