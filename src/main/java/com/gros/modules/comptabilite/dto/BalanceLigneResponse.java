package com.gros.modules.comptabilite.dto;

import java.math.BigDecimal;

public record BalanceLigneResponse(String compteNumero, String intitule, BigDecimal totalDebit,
                                   BigDecimal totalCredit, BigDecimal soldeDebiteur, BigDecimal soldeCrediteur) {}