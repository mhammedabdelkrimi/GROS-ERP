package com.gros.modules.pilotage.service;

import com.gros.modules.facturation.entity.FactureClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public final class CalculsPilotage {
    private static final BigDecimal CENT = BigDecimal.valueOf(100);

    private CalculsPilotage() {}

    public record Tranches(BigDecimal de1a30, BigDecimal de31a60, BigDecimal plusDe60, int nombre) {}

    /** Variation en % de courant par rapport à précédent. Null si précédent est absent ou nul. */
    public static BigDecimal variationPourcent(BigDecimal courant, BigDecimal precedent) {
        if (courant == null || precedent == null || precedent.signum() == 0) return null;
        return courant.subtract(precedent).multiply(CENT).divide(precedent, 2, RoundingMode.HALF_UP);
    }

    /** Taux de marge = marge / chiffre d'affaires, en %. Null si le chiffre d'affaires est nul. */
    public static BigDecimal tauxMarge(BigDecimal marge, BigDecimal ca) {
        if (ca == null || ca.signum() == 0) return null;
        return marge.multiply(CENT).divide(ca, 2, RoundingMode.HALF_UP);
    }

    /** Ventile le reste à payer des factures échues : 1-30, 31-60 et plus de 60 jours de retard. */
    public static Tranches ventiler(List<FactureClient> echues, LocalDate aujourdhui) {
        BigDecimal t1 = BigDecimal.ZERO;
        BigDecimal t2 = BigDecimal.ZERO;
        BigDecimal t3 = BigDecimal.ZERO;
        int nombre = 0;
        for (FactureClient f : echues) {
            BigDecimal solde = f.soldeRestant();
            long jours = ChronoUnit.DAYS.between(f.getDateEcheance(), aujourdhui);
            if (solde.signum() <= 0 || jours <= 0) continue;
            nombre++;
            if (jours <= 30) t1 = t1.add(solde);
            else if (jours <= 60) t2 = t2.add(solde);
            else t3 = t3.add(solde);
        }
        return new Tranches(t1, t2, t3, nombre);
    }
}