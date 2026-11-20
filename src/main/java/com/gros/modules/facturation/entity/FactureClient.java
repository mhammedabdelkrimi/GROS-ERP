package com.gros.modules.facturation.entity;

import com.gros.common.exception.BusinessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "facture_client") @Getter @Setter @NoArgsConstructor
public class FactureClient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;
    private Long bonLivraisonId;
    private Long clientId;
    private LocalDate dateEmission = LocalDate.now();
    private LocalDate dateEcheance;
    private BigDecimal montantHt = BigDecimal.ZERO;
    private BigDecimal montantTva = BigDecimal.ZERO;
    private BigDecimal montantTtc = BigDecimal.ZERO;
    private BigDecimal montantPaye = BigDecimal.ZERO;
    private BigDecimal montantAvoir = BigDecimal.ZERO;
    private BigDecimal montantRembourse = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING) private StatutFacture statut = StatutFacture.EMISE;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "facture_id", nullable = false)
    private List<LigneFacture> lignes = new ArrayList<>();

    public void calculerTotaux() {
        montantHt = lignes.stream().map(LigneFacture::montantHT).reduce(BigDecimal.ZERO, BigDecimal::add);
        montantTva = lignes.stream().map(LigneFacture::montantTVA).reduce(BigDecimal.ZERO, BigDecimal::add);
        montantTtc = montantHt.add(montantTva);
    }

    /** Montant réellement facturé : TTC moins les avoirs. */
    public BigDecimal montantNet() { return montantTtc.subtract(montantAvoir); }

    /** Ce que le client doit encore sur cette facture (jamais négatif). */
    public BigDecimal soldeRestant() { return montantNet().subtract(montantPaye).max(BigDecimal.ZERO); }

    /** Payé en trop sur cette facture et pas encore remboursé. */
    public BigDecimal creditGenere() {
        return montantPaye.subtract(montantNet()).subtract(montantRembourse).max(BigDecimal.ZERO);
    }

    public boolean estSoldee() { return soldeRestant().signum() == 0; }
    public boolean estEnRetard() { return soldeRestant().signum() > 0 && dateEcheance.isBefore(LocalDate.now()); }

    public void appliquerPaiement(BigDecimal m) {
        if (m.signum() <= 0 || m.compareTo(soldeRestant()) > 0)
            throw new BusinessException("Montant invalide : reste à payer " + soldeRestant());
        montantPaye = montantPaye.add(m);
        statut = estSoldee() ? StatutFacture.PAYEE : StatutFacture.PARTIELLEMENT_PAYEE;
    }

    /** L'avoir corrige le montant facturé : il peut dépasser le reste à payer, mais jamais le net facturé. */
    public void appliquerAvoir(BigDecimal m) {
        if (m.signum() <= 0 || m.compareTo(montantNet()) > 0)
            throw new BusinessException("Montant invalide : avoir maximum " + montantNet());
        montantAvoir = montantAvoir.add(m);
        if (estSoldee()) statut = StatutFacture.PAYEE;
    }

    /** Rembourse une partie du crédit généré par cette facture. */
    public void rembourser(BigDecimal m) {
        if (m.signum() <= 0 || m.compareTo(creditGenere()) > 0)
            throw new BusinessException("Remboursement impossible : crédit disponible sur cette facture " + creditGenere());
        montantRembourse = montantRembourse.add(m);
    }
}