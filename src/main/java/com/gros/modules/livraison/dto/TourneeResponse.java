package com.gros.modules.livraison.dto;

import com.gros.modules.livraison.entity.StatutTournee;

import java.time.LocalDate;
import java.util.List;

public record TourneeResponse(Long id, String numero, LocalDate dateTournee, Long livreurId, String livreurNom,
                              StatutTournee statut, List<BonTourneeResponse> bons) {}