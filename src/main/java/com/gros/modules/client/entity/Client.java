package com.gros.modules.client.entity;

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

@Entity @Table(name = "client")
@Getter @Setter @NoArgsConstructor
public class Client {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String raisonSociale;
    private String adresse;
    private String telephone;
    private BigDecimal limiteCredit = BigDecimal.ZERO;
    private int delaiPaiementJours = 30;
    private BigDecimal remiseHabituelle = BigDecimal.ZERO;
    private boolean bloque;
    private LocalDate dateDeblocage;          // dernier déblocage manuel par la direction
    private boolean actif = true;

    public void desactiver() { actif = false; }
    public void bloquer() { bloque = true; }

    public void debloquer() {
        bloque = false;
        dateDeblocage = LocalDate.now();
    }

    /** Après un déblocage manuel, le blocage automatique laisse un délai de grâce pour régulariser. */
    public boolean estEnGrace(int jours, LocalDate aujourdhui) {
        return dateDeblocage != null && dateDeblocage.plusDays(jours).isAfter(aujourdhui);
    }

    /** Le solde actuel vient de SoldeClientService (partie 11), jamais d'une colonne. */
    public boolean peutAcheterACredit(BigDecimal soldeActuel, BigDecimal nouveauMontant) {
        return !bloque && soldeActuel.add(nouveauMontant).compareTo(limiteCredit) <= 0;
    }
}