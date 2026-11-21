package com.gros.modules.stock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "ligne_inventaire") @Getter @Setter @NoArgsConstructor
public class LigneInventaire {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long produitId;
    private int quantiteTheorique;
    private Integer quantiteComptee;      // null tant que le produit n'est pas compté
    private String justification;

    public LigneInventaire(Long produitId, int quantiteTheorique) {
        this.produitId = produitId;
        this.quantiteTheorique = quantiteTheorique;
    }

    public void compter(int quantite, String justification) {
        this.quantiteComptee = quantite;
        this.justification = justification;
    }

    /** Écart = compté - théorique. Null si pas encore compté. */
    public Integer ecart() {
        return quantiteComptee == null ? null : quantiteComptee - quantiteTheorique;
    }
}