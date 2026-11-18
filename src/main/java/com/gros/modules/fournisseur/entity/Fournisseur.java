package com.gros.modules.fournisseur.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "fournisseur")
@Getter @Setter @NoArgsConstructor
public class Fournisseur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String raisonSociale;
    private String adresse;
    private String telephone;
    private String conditionsPaiement;
    private int delaiLivraisonJours = 7;
    private boolean actif = true;

    public void desactiver() { actif = false; }
}