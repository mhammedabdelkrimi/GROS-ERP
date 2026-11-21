package com.gros.modules.vente.dto;

import com.gros.modules.vente.entity.StatutDevis;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DevisResponse(Long id, String numero, Long clientId, StatutDevis statut,
                            LocalDateTime dateCreation, LocalDate dateValidite, boolean expire,
                            BigDecimal montantHt, BigDecimal montantTva, BigDecimal montantTtc,
                            Long commandeId, List<LigneDevisResponse> lignes) {}