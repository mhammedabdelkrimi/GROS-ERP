package com.gros.modules.produit.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "historique_prix")
@Getter
@Setter
@NoArgsConstructor
public class HistoriquePrix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long produitId;
    private BigDecimal ancienPrix;
    private BigDecimal nouveauPrix;
    private LocalDateTime dateChangement = LocalDateTime.now();
    private Long modifiePar;

    public HistoriquePrix(Long produitId, BigDecimal ancien, BigDecimal nouveau, Long modifiePar) {
        this.produitId = produitId;
        this.ancienPrix = ancien;
        this.nouveauPrix = nouveau;
        this.modifiePar = modifiePar;
    }
}