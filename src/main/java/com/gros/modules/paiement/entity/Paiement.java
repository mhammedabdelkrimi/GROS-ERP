package com.gros.modules.paiement.entity;

import com.gros.common.SecurityUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "paiement") @Getter @Setter @NoArgsConstructor
public class Paiement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long clientId;
    private BigDecimal montant;
    @Enumerated(EnumType.STRING) private ModePaiement modePaiement;
    private LocalDate dateReglement = LocalDate.now();
    private Long creePar = SecurityUtils.idCourant();

    public Paiement(Long clientId, BigDecimal montant, ModePaiement modePaiement) {
        this.clientId = clientId; this.montant = montant; this.modePaiement = modePaiement;
    }
}