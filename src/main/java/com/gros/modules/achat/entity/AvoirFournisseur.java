package com.gros.modules.achat.entity;

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
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "avoir_fournisseur") @Getter @Setter @NoArgsConstructor
public class AvoirFournisseur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;               // AF-2026-000001
    private String numeroFournisseur;    // numéro de l'avoir envoyé par le fournisseur
    private Long factureId;
    private Long fournisseurId;
    private String motif;
    private BigDecimal montantHt;
    private BigDecimal montantTva;
    private BigDecimal montantTtc;
    private LocalDate dateAvoir;
    private Long creePar = SecurityUtils.idCourant();
    private LocalDateTime dateCreation = LocalDateTime.now();

    public AvoirFournisseur(String numero, String numeroFournisseur, Long factureId, Long fournisseurId,
                            String motif, BigDecimal ht, BigDecimal tva, LocalDate dateAvoir) {
        this.numero = numero;
        this.numeroFournisseur = numeroFournisseur;
        this.factureId = factureId;
        this.fournisseurId = fournisseurId;
        this.motif = motif;
        this.montantHt = ht;
        this.montantTva = tva;
        this.montantTtc = ht.add(tva);
        this.dateAvoir = dateAvoir;
    }
}