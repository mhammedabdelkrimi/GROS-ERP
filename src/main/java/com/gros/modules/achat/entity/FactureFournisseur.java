package com.gros.modules.achat.entity;

import com.gros.common.SecurityUtils;
import com.gros.common.exception.BusinessException;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "facture_fournisseur") @Getter @Setter @NoArgsConstructor
public class FactureFournisseur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;              // numéro interne FF-2026-000001
    private String numeroFournisseur;   // numéro porté par la facture du fournisseur
    private Long commandeId;
    private Long fournisseurId;
    private LocalDate dateFacture;
    private LocalDate dateEcheance;
    private BigDecimal montantHt = BigDecimal.ZERO;
    private BigDecimal montantTva = BigDecimal.ZERO;
    private BigDecimal montantTtc = BigDecimal.ZERO;
    private BigDecimal montantAvoirHt = BigDecimal.ZERO;
    private BigDecimal montantAvoirTva = BigDecimal.ZERO;
    private BigDecimal montantAvoirTtc = BigDecimal.ZERO;
    private BigDecimal montantPaye = BigDecimal.ZERO;
    private BigDecimal montantRembourse = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) private StatutFactureFournisseur statut = StatutFactureFournisseur.A_PAYER;
    private Long creePar = SecurityUtils.idCourant();
    private LocalDateTime dateCreation = LocalDateTime.now();

    /** Montant réellement dû au fournisseur : TTC moins les avoirs. */
    public BigDecimal netTtc() { return montantTtc.subtract(montantAvoirTtc); }

    /** Ce que nous devons encore (jamais négatif). */
    public BigDecimal resteAPayer() { return netTtc().subtract(montantPaye).max(BigDecimal.ZERO); }

    /** Payé en trop après un avoir et pas encore remboursé par le fournisseur. */
    public BigDecimal creditFournisseur() {
        return montantPaye.subtract(netTtc()).subtract(montantRembourse).max(BigDecimal.ZERO);
    }

    public boolean estEnRetard() {
        return resteAPayer().signum() > 0 && dateEcheance != null && dateEcheance.isBefore(LocalDate.now());
    }

    public void appliquerPaiement(BigDecimal m) {
        if (m.signum() <= 0 || m.compareTo(resteAPayer()) > 0)
            throw new BusinessException("Montant invalide : reste à payer " + resteAPayer());
        montantPaye = montantPaye.add(m);
        actualiserStatut();
    }

    /** L'avoir corrige le montant facturé. HT et TVA sont bornés par ce qui reste de la facture. */
    public void appliquerAvoir(BigDecimal ht, BigDecimal tva) {
        if (ht.signum() <= 0 || tva.signum() < 0)
            throw new BusinessException("Montants d'avoir invalides");
        BigDecimal htRestant = montantHt.subtract(montantAvoirHt);
        if (ht.compareTo(htRestant) > 0)
            throw new BusinessException("Avoir HT supérieur au HT restant de la facture : " + htRestant);
        BigDecimal tvaRestante = montantTva.subtract(montantAvoirTva);
        BigDecimal tvaProportionnelle = montantHt.signum() == 0 ? BigDecimal.ZERO
                : ht.multiply(montantTva).divide(montantHt, 2, RoundingMode.HALF_UP);
        BigDecimal tvaMax = tvaProportionnelle.min(tvaRestante);
        if (tva.compareTo(tvaMax) > 0)
            throw new BusinessException("TVA d'avoir supérieure au maximum attendu : " + tvaMax);
        montantAvoirHt = montantAvoirHt.add(ht);
        montantAvoirTva = montantAvoirTva.add(tva);
        montantAvoirTtc = montantAvoirHt.add(montantAvoirTva);
        actualiserStatut();
    }

    /** Le fournisseur nous rembourse une partie (ou la totalité) du trop-payé. */
    public void rembourser(BigDecimal m) {
        if (m.signum() <= 0 || m.compareTo(creditFournisseur()) > 0)
            throw new BusinessException("Remboursement impossible : crédit fournisseur sur cette facture "
                    + creditFournisseur());
        montantRembourse = montantRembourse.add(m);
    }

    private void actualiserStatut() {
        if (resteAPayer().signum() == 0) statut = StatutFactureFournisseur.PAYEE;
        else if (montantPaye.signum() > 0) statut = StatutFactureFournisseur.PARTIELLEMENT_PAYEE;
    }
}