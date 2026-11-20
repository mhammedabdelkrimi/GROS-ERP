package com.gros.modules.paiement.dto;

import java.math.BigDecimal;
import java.util.List;

/** creditTotal = somme des crédits remboursables par facture. solde = situation globale (négatif = crédit net). */
public record CreditClientResponse(Long clientId, BigDecimal creditTotal, BigDecimal solde,
                                   List<CreditFactureResponse> factures) {}