package com.gros.modules.pilotage.service;

import com.gros.modules.facturation.entity.FactureClient;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CalculsPilotageTest {
    private static final LocalDate AUJ = LocalDate.of(2026, 11, 21);

    private static BigDecimal d(String v) { return new BigDecimal(v); }

    private static FactureClient facture(String ttc, LocalDate echeance) {
        var f = new FactureClient();
        f.setMontantTtc(d(ttc));
        f.setDateEcheance(echeance);
        return f;
    }

    @Test void variationPositive() {
        assertEquals(0, d("25.00").compareTo(CalculsPilotage.variationPourcent(d("1250"), d("1000"))));
    }

    @Test void variationNulleSiPrecedentZeroOuAbsent() {
        assertNull(CalculsPilotage.variationPourcent(d("100"), BigDecimal.ZERO));
        assertNull(CalculsPilotage.variationPourcent(d("100"), null));
    }

    @Test void tauxMargeEnPourcentage() {
        assertEquals(0, d("23.08").compareTo(CalculsPilotage.tauxMarge(d("150"), d("650"))));
        assertNull(CalculsPilotage.tauxMarge(d("10"), BigDecimal.ZERO));
    }

    @Test void impayesVentilesParAnciennete() {
        var echues = List.of(
                facture("100", AUJ.minusDays(10)),    // 10 jours de retard
                facture("200", AUJ.minusDays(51)),    // 51 jours
                facture("300", AUJ.minusDays(112)));  // 112 jours
        var t = CalculsPilotage.ventiler(echues, AUJ);
        assertEquals(0, d("100").compareTo(t.de1a30()));
        assertEquals(0, d("200").compareTo(t.de31a60()));
        assertEquals(0, d("300").compareTo(t.plusDe60()));
        assertEquals(3, t.nombre());
    }

    @Test void factureSoldeeOuNonEchueEstIgnoree() {
        var soldee = facture("100", AUJ.minusDays(40));
        soldee.appliquerPaiement(d("100"));
        var nonEchue = facture("100", AUJ.plusDays(5));
        var t = CalculsPilotage.ventiler(List.of(soldee, nonEchue), AUJ);
        assertEquals(0, t.nombre());
        assertEquals(0, BigDecimal.ZERO.compareTo(t.de31a60()));
    }
}