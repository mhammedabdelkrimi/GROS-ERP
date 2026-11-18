package com.gros.common.numerotation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "compteur")
@Getter
@Setter
@NoArgsConstructor
public class Compteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeDoc;
    private int annee;
    private long derniereValeur;

    public Compteur(String typeDoc, int annee) {
        this.typeDoc = typeDoc;
        this.annee = annee;
    }
}