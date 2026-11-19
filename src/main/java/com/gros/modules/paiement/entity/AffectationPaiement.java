package com.gros.modules.paiement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity @Table(name = "affectation_paiement") @Getter @Setter @NoArgsConstructor
public class AffectationPaiement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long paiementId;
    private Long factureId;
    private BigDecimal montantAffecte;

    public AffectationPaiement(Long paiementId, Long factureId, BigDecimal montantAffecte) {
        this.paiementId = paiementId; this.factureId = factureId; this.montantAffecte = montantAffecte;
    }
}