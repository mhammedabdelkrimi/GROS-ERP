package com.gros.modules.vente.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity @Table(name = "ligne_devis") @Getter @Setter @NoArgsConstructor
public class LigneDevis {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long produitId;
    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal tauxTva;

    public LigneDevis(Long produitId, int quantite, BigDecimal prixUnitaire, BigDecimal tauxTva) {
        this.produitId = produitId;
        this.quantite = quantite;
        this.prixUnitaire = prixUnitaire;
        this.tauxTva = tauxTva;
    }

    public BigDecimal montantHT() {
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal montantTVA() {
        return montantHT().multiply(tauxTva).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}