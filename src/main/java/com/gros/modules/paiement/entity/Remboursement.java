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
import java.time.LocalDateTime;

@Entity @Table(name = "remboursement") @Getter @Setter @NoArgsConstructor
public class Remboursement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long clientId;
    private Long factureId;
    private BigDecimal montant;
    @Enumerated(EnumType.STRING) private ModePaiement modePaiement;
    private LocalDateTime dateRemboursement = LocalDateTime.now();
    private Long creePar = SecurityUtils.idCourant();

    public Remboursement(Long clientId, Long factureId, BigDecimal montant, ModePaiement modePaiement) {
        this.clientId = clientId;
        this.factureId = factureId;
        this.montant = montant;
        this.modePaiement = modePaiement;
    }
}