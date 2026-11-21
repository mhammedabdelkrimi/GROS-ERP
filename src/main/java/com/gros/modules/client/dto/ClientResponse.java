package com.gros.modules.client.dto;

import java.math.BigDecimal;

public record ClientResponse(Long id, String raisonSociale, String adresse, String telephone,
                             BigDecimal limiteCredit, int delaiPaiementJours, BigDecimal remiseHabituelle,
                             boolean bloque, boolean actif) {}