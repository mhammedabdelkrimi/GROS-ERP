package com.gros.modules.pilotage.dto;

import java.math.BigDecimal;
import java.util.List;

public record CaAnnuelResponse(int annee, List<CaMoisResponse> mois, BigDecimal totalNetHt, BigDecimal totalNetTtc) {}