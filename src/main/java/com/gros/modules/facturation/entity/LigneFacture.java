package com.gros.modules.facturation.entity;

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

@Entity @Table(name = "ligne_facture") @Getter @Setter @NoArgsConstructor
public class LigneFacture {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long produitId;
    private String designation;
    private int quantite;
    private BigDecimal prixUnitaire;
    private BigDecimal remisePourcent;
    private BigDecimal tauxTva;
    private BigDecimal coutUnitaire = BigDecimal.ZERO;   // prix d'achat figé au moment de la facture

    public LigneFacture(Long produitId, String designation, int quantite, BigDecimal prixUnitaire,
                        BigDecimal remisePourcent, BigDecimal tauxTva) {
        this.produitId = produitId; this.designation = designation; this.quantite = quantite;
        this.prixUnitaire = prixUnitaire; this.remisePourcent = remisePourcent; this.tauxTva = tauxTva;
    }

    public LigneFacture(Long produitId, String designation, int quantite, BigDecimal prixUnitaire,
                        BigDecimal remisePourcent, BigDecimal tauxTva, BigDecimal coutUnitaire) {
        this(produitId, designation, quantite, prixUnitaire, remisePourcent, tauxTva);
        this.coutUnitaire = coutUnitaire;
    }

    public BigDecimal montantHT() {
        BigDecimal facteur = BigDecimal.ONE.subtract(remisePourcent.divide(BigDecimal.valueOf(100)));
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite)).multiply(facteur).setScale(2, RoundingMode.HALF_UP);
    }
    public BigDecimal montantTVA() {
        return montantHT().multiply(tauxTva).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}