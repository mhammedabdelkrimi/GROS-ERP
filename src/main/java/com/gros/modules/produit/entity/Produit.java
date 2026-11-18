package com.gros.modules.produit.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "produit")
@Getter
@Setter
@NoArgsConstructor
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reference;
    private String designation;
    private Long categorieId;
    private String unite;
    private BigDecimal prixAchat;
    private BigDecimal prixVente;
    private BigDecimal tauxTva;
    private int seuilAlerte;
    private boolean perissable;
    private boolean actif = true;

    public void desactiver() { this.actif = false; }          // suppression logique (BNF-13)

    public BigDecimal margeUnitaire() { return prixVente.subtract(prixAchat); }
}