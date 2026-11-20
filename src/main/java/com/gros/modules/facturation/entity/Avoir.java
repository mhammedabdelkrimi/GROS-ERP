package com.gros.modules.facturation.entity;

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

@Entity @Table(name = "avoir") @Getter @Setter @NoArgsConstructor
public class Avoir {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;
    private Long factureId;
    private String motif;
    private BigDecimal montant;
    private LocalDateTime dateCreation = LocalDateTime.now();
    private Long creePar = SecurityUtils.idCourant();

    public Avoir(String numero, Long factureId, String motif, BigDecimal montant) {
        this.numero = numero;
        this.factureId = factureId;
        this.motif = motif;
        this.montant = montant;
    }
}