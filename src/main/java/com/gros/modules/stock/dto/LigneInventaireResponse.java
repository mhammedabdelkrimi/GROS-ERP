package com.gros.modules.stock.dto;

public record LigneInventaireResponse(Long produitId, String reference, String designation,
                                      int quantiteTheorique, Integer quantiteComptee,
                                      Integer ecart, String justification) {}