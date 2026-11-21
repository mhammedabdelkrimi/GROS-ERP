package com.gros.modules.comptabilite.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity @Table(name = "ligne_ecriture") @Immutable
@Getter @Setter @NoArgsConstructor
public class LigneEcriture {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long ecritureId;
    private String compteNumero;
    private BigDecimal debit = BigDecimal.ZERO;
    private BigDecimal credit = BigDecimal.ZERO;

    public static LigneEcriture auDebit(String compte, BigDecimal montant) {
        LigneEcriture l = new LigneEcriture();
        l.compteNumero = compte;
        l.debit = montant;
        return l;
    }

    public static LigneEcriture auCredit(String compte, BigDecimal montant) {
        LigneEcriture l = new LigneEcriture();
        l.compteNumero = compte;
        l.credit = montant;
        return l;
    }

    /** Ligne inverse (débit et crédit échangés) pour une écriture d'extourne. */
    public LigneEcriture inverse() {
        LigneEcriture l = new LigneEcriture();
        l.compteNumero = compteNumero;
        l.debit = credit;
        l.credit = debit;
        return l;
    }
}