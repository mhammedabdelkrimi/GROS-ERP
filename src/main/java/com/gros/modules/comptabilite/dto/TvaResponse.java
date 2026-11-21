package com.gros.modules.comptabilite.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** tvaDue = tvaFacturee - tvaRecuperable. Négative = crédit de TVA. */
public record TvaResponse(int annee, int mois, LocalDate du, LocalDate au,
                          BigDecimal tvaFacturee, BigDecimal tvaRecuperable, BigDecimal tvaDue,
                          boolean cloturee) {}