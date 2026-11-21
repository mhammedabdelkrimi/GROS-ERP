package com.gros.modules.comptabilite.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PeriodeResponse(int annee, int mois, BigDecimal tvaFacturee, BigDecimal tvaRecuperable,
                              BigDecimal tvaDue, LocalDateTime dateCloture) {}