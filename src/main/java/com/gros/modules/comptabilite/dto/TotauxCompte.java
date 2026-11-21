package com.gros.modules.comptabilite.dto;

import java.math.BigDecimal;

public record TotauxCompte(String compteNumero, BigDecimal debit, BigDecimal credit) {}