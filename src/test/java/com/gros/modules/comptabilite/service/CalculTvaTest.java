package com.gros.modules.comptabilite.service;

import com.gros.modules.comptabilite.dto.TotauxCompte;
import com.gros.modules.comptabilite.dto.TvaResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculTvaTest {
    private static final YearMonth NOV = YearMonth.of(2026, 11);

    private static BigDecimal d(String v) { return new BigDecimal(v); }

    @Test void tvaFactureeEstLeCreditNetDu4455() {
        var totaux = List.of(new TotauxCompte("4455", d("26"), d("260")));
        TvaResponse t = CalculTva.calculer(NOV, totaux, false);
        assertEquals(0, d("234").compareTo(t.tvaFacturee()));
        assertEquals(0, d("234").compareTo(t.tvaDue()));
    }

    @Test void tvaDueEstFactureeMoinsRecuperable() {
        var totaux = List.of(new TotauxCompte("4455", BigDecimal.ZERO, d("260")),
                new TotauxCompte("3455", d("60"), BigDecimal.ZERO));
        TvaResponse t = CalculTva.calculer(NOV, totaux, false);
        assertEquals(0, d("200").compareTo(t.tvaDue()));
    }

    @Test void periodeSansEcritureDonneZeroEtBornesDuMois() {
        TvaResponse t = CalculTva.calculer(NOV, List.of(), true);
        assertEquals(0, BigDecimal.ZERO.compareTo(t.tvaDue()));
        assertEquals(LocalDate.of(2026, 11, 1), t.du());
        assertEquals(LocalDate.of(2026, 11, 30), t.au());
        assertTrue(t.cloturee());
    }
}