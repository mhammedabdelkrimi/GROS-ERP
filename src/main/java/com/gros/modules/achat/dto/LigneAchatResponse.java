package com.gros.modules.achat.dto;

import java.math.BigDecimal;

public record LigneAchatResponse(Long produitId, int quantite, int quantiteRecue, BigDecimal prixUnitaire) {}