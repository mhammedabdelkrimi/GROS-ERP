package com.gros.modules.comptabilite.entity;

import com.gros.common.SecurityUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Une ligne = une période clôturée, avec la TVA figée au moment de la clôture et son règlement éventuel. */
@Entity @Table(name = "periode_comptable") @Getter @Setter @NoArgsConstructor
public class PeriodeComptable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private int annee;
    private int mois;
    private BigDecimal tvaFacturee;
    private BigDecimal tvaRecuperable;
    private BigDecimal tvaDue;
    private BigDecimal tvaReglee = BigDecimal.ZERO;
    private Long cloturePar = SecurityUtils.idCourant();
    private LocalDateTime dateCloture = LocalDateTime.now();
    private LocalDateTime dateReglement;

    public PeriodeComptable(int annee, int mois, BigDecimal tvaFacturee, BigDecimal tvaRecuperable, BigDecimal tvaDue) {
        this.annee = annee;
        this.mois = mois;
        this.tvaFacturee = tvaFacturee;
        this.tvaRecuperable = tvaRecuperable;
        this.tvaDue = tvaDue;
    }
}