package com.gros.modules.pilotage.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ImpayesResponse(LocalDate aLaDate, BigDecimal nonEchu, BigDecimal echu1a30,
                              BigDecimal echu31a60, BigDecimal echuPlus60, BigDecimal totalEchu,
                              BigDecimal totalImpaye, int nombreFacturesEchues) {}