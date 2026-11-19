package com.gros.modules.achat.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity @Table(name = "ligne_reception") @Getter @Setter @NoArgsConstructor
public class LigneReception {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long produitId;
    private int quantiteRecue;
    private String numeroLot;
    private LocalDate datePeremption;

    public LigneReception(Long produitId, int quantiteRecue, String numeroLot, LocalDate datePeremption) {
        this.produitId = produitId; this.quantiteRecue = quantiteRecue;
        this.numeroLot = numeroLot; this.datePeremption = datePeremption;
    }
}