package com.gros.modules.vente.entity;

import com.gros.common.SecurityUtils;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "devis") @Getter @Setter @NoArgsConstructor
public class Devis {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;
    private Long clientId;
    @Enumerated(EnumType.STRING) private StatutDevis statut = StatutDevis.BROUILLON;
    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDate dateValidite;
    private BigDecimal montantHt = BigDecimal.ZERO;
    private BigDecimal montantTva = BigDecimal.ZERO;
    private BigDecimal montantTtc = BigDecimal.ZERO;
    private Long commandeId;
    private Long creePar = SecurityUtils.idCourant();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "devis_id", nullable = false)
    private List<LigneDevis> lignes = new ArrayList<>();

    public void calculerTotaux() {
        montantHt = lignes.stream().map(LigneDevis::montantHT).reduce(BigDecimal.ZERO, BigDecimal::add);
        montantTva = lignes.stream().map(LigneDevis::montantTVA).reduce(BigDecimal.ZERO, BigDecimal::add);
        montantTtc = montantHt.add(montantTva);
    }

    /** Un devis expire s'il n'est pas encore tranché (ou converti) et que sa date de validité est passée. */
    public boolean estExpire() {
        boolean enCours = statut == StatutDevis.BROUILLON || statut == StatutDevis.ENVOYE || statut == StatutDevis.ACCEPTE;
        return enCours && dateValidite != null && dateValidite.isBefore(LocalDate.now());
    }

    public void verifierNonExpire() {
        if (estExpire()) throw new BusinessException("Devis expiré depuis le " + dateValidite);
    }

    public void envoyer() { verifierNonExpire(); changerStatut(StatutDevis.ENVOYE); }

    public void accepter() { verifierNonExpire(); changerStatut(StatutDevis.ACCEPTE); }

    public void refuser() { changerStatut(StatutDevis.REFUSE); }

    public void verifierConvertible() {
        if (statut != StatutDevis.ACCEPTE)
            throw new BusinessException("Seul un devis accepté peut être converti en commande (statut actuel : " + statut + ")");
        verifierNonExpire();
    }

    public void marquerConverti(Long commandeId) {
        changerStatut(StatutDevis.CONVERTI);
        this.commandeId = commandeId;
    }

    private void changerStatut(StatutDevis cible) {
        if (!statut.peutPasserA(cible))
            throw new BusinessException("Transition interdite : " + statut + " vers " + cible);
        statut = cible;
    }
}