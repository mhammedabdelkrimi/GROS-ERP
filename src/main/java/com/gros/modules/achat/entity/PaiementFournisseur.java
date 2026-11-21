package com.gros.modules.achat.entity;

import com.gros.common.SecurityUtils;
import com.gros.modules.paiement.entity.ModePaiement;
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

@Entity @Table(name = "paiement_fournisseur") @Getter @Setter @NoArgsConstructor
public class PaiementFournisseur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long factureId;
    private BigDecimal montant;
    @Enumerated(EnumType.STRING) private ModePaiement modePaiement;
    private LocalDate dateReglement = LocalDate.now();
    private Long creePar = SecurityUtils.idCourant();

    public PaiementFournisseur(Long factureId, BigDecimal montant, ModePaiement modePaiement) {
        this.factureId = factureId;
        this.montant = montant;
        this.modePaiement = modePaiement;
    }
}