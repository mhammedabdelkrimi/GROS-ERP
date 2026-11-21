package com.gros.modules.pilotage.dto;

import java.math.BigDecimal;

public record ClientPerformanceResponse(Long clientId, String raisonSociale, int nombreFactures,
                                        BigDecimal caHt, BigDecimal cout, BigDecimal marge,
                                        BigDecimal tauxMarge) {}