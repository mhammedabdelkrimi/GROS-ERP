package com.gros.modules.comptabilite.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record EcritureResponse(Long id, String numero, LocalDate dateEcriture, String libelle,
                               String pieceType, Long pieceId, Long extourneDe,
                               BigDecimal totalDebit, BigDecimal totalCredit,
                               List<LigneEcritureResponse> lignes) {}