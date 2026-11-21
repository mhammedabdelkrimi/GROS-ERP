package com.gros.modules.pilotage.dto;

import java.math.BigDecimal;

public record ClientImpayeResponse(Long clientId, String raisonSociale, int nombreFactures,
                                   BigDecimal montantImpaye) {}