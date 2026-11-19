package com.gros.modules.achat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity @Table(name = "ligne_cmd_fournisseur") @Getter @Setter @NoArgsConstructor
public class LigneCommandeFournisseur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long produitId;
    private int quantite;
    private int quantiteRecue;
    private BigDecimal prixUnitaire;

    public LigneCommandeFournisseur(Long produitId, int quantite, BigDecimal prixUnitaire) {
        this.produitId = produitId; this.quantite = quantite; this.prixUnitaire = prixUnitaire;
    }
    public BigDecimal montant() { return prixUnitaire.multiply(BigDecimal.valueOf(quantite)); }
}