package com.gros.modules.vente.entity;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "commande_client") @Getter @Setter @NoArgsConstructor
public class CommandeClient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;
    private Long clientId;
    @Enumerated(EnumType.STRING) private StatutCommande statut = StatutCommande.BROUILLON;
    private BigDecimal montantHt = BigDecimal.ZERO;
    private BigDecimal montantTva = BigDecimal.ZERO;
    private BigDecimal montantTtc = BigDecimal.ZERO;
    private Long creePar;
    private LocalDateTime dateCreation = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "commande_id", nullable = false)
    private List<LigneCommandeClient> lignes = new ArrayList<>();

    public void calculerTotaux() {
        montantHt = lignes.stream().map(LigneCommandeClient::montantHT).reduce(BigDecimal.ZERO, BigDecimal::add);
        montantTva = lignes.stream().map(LigneCommandeClient::montantTVA).reduce(BigDecimal.ZERO, BigDecimal::add);
        montantTtc = montantHt.add(montantTva);
    }

    public void changerStatut(StatutCommande cible) {
        if (!statut.peutPasserA(cible))
            throw new BusinessException("Transition interdite : " + statut + " vers " + cible);
        statut = cible;
    }
}