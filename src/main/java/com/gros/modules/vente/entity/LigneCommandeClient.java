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

@Entity @Table(name = "ligne_commande_client") @Getter @Setter @NoArgsConstructor
public class LigneCommandeClient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long produitId;
    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal remisePourcent = BigDecimal.ZERO;
    private BigDecimal tauxTva;

    public LigneCommandeClient(Long produitId, int quantite, BigDecimal prix, BigDecimal tva) {
        this.produitId = produitId; this.quantite = quantite; this.prixUnitaire = prix; this.tauxTva = tva;
    }

    public BigDecimal montantHT() {
        BigDecimal facteur = BigDecimal.ONE.subtract(remisePourcent.divide(BigDecimal.valueOf(100)));
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite)).multiply(facteur).setScale(2, RoundingMode.HALF_UP);
    }
    public BigDecimal montantTVA() {
        return montantHT().multiply(tauxTva).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
    public BigDecimal montantTTC() { return montantHT().add(montantTVA()); }
}