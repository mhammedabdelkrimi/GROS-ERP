package com.gros.modules.livraison.dto;

import com.gros.modules.livraison.entity.StatutBon;

public record BonTourneeResponse(Long bonId, String numero, StatutBon statut, boolean signeParClient,
                                 Long commandeId, Long clientId, String raisonSociale,
                                 String adresse, String telephone) {}