package com.gros.modules.achat.entity;

import com.gros.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FactureFournisseurTest {

    private FactureFournisseur facture() {
        var f = new FactureFournisseur();
        f.setMontantTtc(new BigDecimal("6000"));
        f.setDateEcheance(LocalDate.now().plusDays(30));
        return f;
    }

    @Test void paiementPartielPuisTotal() {
        var f = facture();
        f.appliquerPaiement(new BigDecimal("2000"));
        assertEquals(StatutFactureFournisseur.PARTIELLEMENT_PAYEE, f.getStatut());
        f.appliquerPaiement(new BigDecimal("4000"));
        assertEquals(StatutFactureFournisseur.PAYEE, f.getStatut());
    }

    @Test void paiementSuperieurAuResteEstRefuse() {
        var f = facture();
        f.appliquerPaiement(new BigDecimal("2000"));
        assertThrows(BusinessException.class, () -> f.appliquerPaiement(new BigDecimal("4001")));
    }

    @Test void factureEchueNonPayeeEstEnRetard() {
        var f = facture();
        assertFalse(f.estEnRetard());
        f.setDateEcheance(LocalDate.now().minusDays(1));
        assertTrue(f.estEnRetard());
    }
}