package com.gros.modules.facturation.entity;

import com.gros.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FactureClientTest {
    @Test void paiementPartielPuisTotal() {
        var f = new FactureClient();
        f.setMontantTtc(new BigDecimal("1200"));
        f.appliquerPaiement(new BigDecimal("500"));
        assertEquals(StatutFacture.PARTIELLEMENT_PAYEE, f.getStatut());
        f.appliquerPaiement(new BigDecimal("700"));
        assertEquals(StatutFacture.PAYEE, f.getStatut());
    }

    @Test void refuseUnPaiementSuperieurAuSolde() {
        var f = new FactureClient();
        f.setMontantTtc(new BigDecimal("840"));
        assertThrows(BusinessException.class, () -> f.appliquerPaiement(new BigDecimal("1000")));
    }
}