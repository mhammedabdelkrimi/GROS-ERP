package com.gros.modules.livraison.entity;

import com.gros.common.SecurityUtils;
import com.gros.common.exception.BusinessException;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity @Table(name = "tournee") @Getter @Setter @NoArgsConstructor
public class Tournee {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;
    private LocalDate dateTournee;
    private Long livreurId;
    @Enumerated(EnumType.STRING) private StatutTournee statut = StatutTournee.PLANIFIEE;
    private Long creePar = SecurityUtils.idCourant();
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ElementCollection
    @CollectionTable(name = "tournee_bon", joinColumns = @JoinColumn(name = "tournee_id"))
    @Column(name = "bon_livraison_id")
    private Set<Long> bonIds = new LinkedHashSet<>();

    public void ajouterBon(Long bonId) {
        verifierModifiable();
        bonIds.add(bonId);
    }

    public void retirerBon(Long bonId) {
        verifierModifiable();
        if (!bonIds.remove(bonId))
            throw new BusinessException("Ce bon ne fait pas partie de la tournée");
    }

    public void demarrer() {
        if (statut != StatutTournee.PLANIFIEE)
            throw new BusinessException("Seule une tournée planifiée peut démarrer (statut actuel : " + statut + ")");
        if (bonIds.isEmpty())
            throw new BusinessException("Une tournée sans bon de livraison ne peut pas démarrer");
        statut = StatutTournee.EN_COURS;
    }

    public void terminer() {
        if (statut != StatutTournee.EN_COURS)
            throw new BusinessException("Seule une tournée en cours peut être terminée (statut actuel : " + statut + ")");
        statut = StatutTournee.TERMINEE;
    }

    private void verifierModifiable() {
        if (statut != StatutTournee.PLANIFIEE)
            throw new BusinessException("La tournée est déjà " + statut + " : elle ne peut plus être modifiée");
    }
}