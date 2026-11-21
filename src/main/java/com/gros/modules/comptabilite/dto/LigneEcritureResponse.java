package com.gros.modules.comptabilite.dto;

import java.math.BigDecimal;

public record LigneEcritureResponse(String compteNumero, String intitule, BigDecimal debit, BigDecimal credit) {}