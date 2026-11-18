package com.gros.modules.fournisseur.dto;

public record FournisseurResponse(Long id, String raisonSociale, String adresse, String telephone,
                                  String conditionsPaiement, int delaiLivraisonJours, boolean actif) {}