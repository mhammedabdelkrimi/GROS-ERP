package com.gros.modules.paiement.dto;

import java.math.BigDecimal;

public record CreditFactureResponse(Long factureId, String numero, BigDecimal credit) {}