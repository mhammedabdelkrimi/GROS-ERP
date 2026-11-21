package com.gros.modules.stock.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record InventaireResponse(Long id, String numero, LocalDate dateInventaire, boolean cloture,
                                 LocalDateTime dateCloture, List<LigneInventaireResponse> lignes) {}