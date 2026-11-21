package com.gros.modules.comptabilite.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BalanceResponse(LocalDate du, LocalDate au, List<BalanceLigneResponse> comptes,
                              BigDecimal totalDebit, BigDecimal totalCredit) {}