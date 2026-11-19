package com.gros.modules.achat.entity;

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

@Entity @Table(name = "commande_fournisseur") @Getter @Setter @NoArgsConstructor
public class CommandeFournisseur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;
    private Long fournisseurId;
    @Enumerated(EnumType.STRING) private StatutCommandeAchat statut = StatutCommandeAchat.BROUILLON;
    private BigDecimal montantTotal = BigDecimal.ZERO;
    private Long creePar;
    private LocalDateTime dateCreation = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "commande_id", nullable = false)
    private List<LigneCommandeFournisseur> lignes = new ArrayList<>();

    public void ajouterLigne(Long produitId, int quantite, BigDecimal prix) {
        lignes.add(new LigneCommandeFournisseur(produitId, quantite, prix));
    }
    public void calculerTotal() {
        montantTotal = lignes.stream().map(LigneCommandeFournisseur::montant).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public boolean estCompletementRecue() {
        return lignes.stream().allMatch(l -> l.getQuantiteRecue() >= l.getQuantite());
    }
}