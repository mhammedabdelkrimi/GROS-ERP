package com.gros.modules.pilotage.dto;

import java.math.BigDecimal;

public record SyntheseResponse(int annee, int mois, BigDecimal caNetHtMois, BigDecimal caNetHtMoisPrecedent,
                               BigDecimal variationPourcent, BigDecimal margeMois, BigDecimal tauxMargeMois,
                               BigDecimal totalImpaye, BigDecimal totalEchu, BigDecimal valeurStock,
                               int produitsEnAlerte) {}