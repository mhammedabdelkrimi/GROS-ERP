package com.gros.modules.facturation.entity;

import com.gros.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FactureClientAvoirTest {

    private FactureClient facture(String ttc) {
        var f = new FactureClient();
        f.setMontantTtc(new BigDecimal(ttc));
        return f;
    }

    @Test void avoirReduitLeResteDu() {
        var f = facture("1200");
        f.appliquerAvoir(new BigDecimal("200"));
        assertEquals(0, new BigDecimal("1000").compareTo(f.soldeRestant()));
        assertEquals(StatutFacture.EMISE, f.getStatut());
    }

    @Test void avoirApresPaiementTotalGenereUnCredit() {
        var f = facture("780");
        f.appliquerPaiement(new BigDecimal("780"));
        f.appliquerAvoir(new BigDecimal("156"));
        assertEquals(0, BigDecimal.ZERO.compareTo(f.soldeRestant()));
        assertEquals(0, new BigDecimal("156").compareTo(f.creditGenere()));
        assertEquals(StatutFacture.PAYEE, f.getStatut());
    }

    @Test void avoirSuperieurAuNetFactureEstRefuse() {
        var f = facture("1200");
        f.appliquerAvoir(new BigDecimal("1000"));
        assertThrows(BusinessException.class, () -> f.appliquerAvoir(new BigDecimal("201")));
    }

    @Test void avoirTotalSansPaiementSoldeLaFacture() {
        var f = facture("1200");
        f.appliquerAvoir(new BigDecimal("1200"));
        assertEquals(StatutFacture.PAYEE, f.getStatut());
        assertEquals(0, BigDecimal.ZERO.compareTo(f.creditGenere()));
    }

    @Test void paiementApresAvoirNeDepassePasLeResteDu() {
        var f = facture("1200");
        f.appliquerAvoir(new BigDecimal("200"));
        assertThrows(BusinessException.class, () -> f.appliquerPaiement(new BigDecimal("1001")));
    }
}