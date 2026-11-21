package com.gros.modules.achat.entity;

import com.gros.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FactureFournisseurAvoirTest {

    private static BigDecimal d(String v) { return new BigDecimal(v); }

    private FactureFournisseur facture() {
        var f = new FactureFournisseur();
        f.setMontantHt(d("5000"));
        f.setMontantTva(d("1000"));
        f.setMontantTtc(d("6000"));
        return f;
    }

    @Test void avoirReduitLeResteAPayer() {
        var f = facture();
        f.appliquerAvoir(d("500"), d("100"));
        assertEquals(0, d("5400").compareTo(f.resteAPayer()));
        assertEquals(StatutFactureFournisseur.A_PAYER, f.getStatut());
    }

    @Test void avoirSurFactureReglee_genereUnCreditPuisRemboursement() {
        var f = facture();
        f.appliquerPaiement(d("6000"));
        f.appliquerAvoir(d("1000"), d("200"));
        assertEquals(0, d("1200").compareTo(f.creditFournisseur()));
        assertEquals(StatutFactureFournisseur.PAYEE, f.getStatut());
        f.rembourser(d("1200"));
        assertEquals(0, BigDecimal.ZERO.compareTo(f.creditFournisseur()));
    }

    @Test void avoirSuperieurAuHtRestantEstRefuse() {
        var f = facture();
        assertThrows(BusinessException.class, () -> f.appliquerAvoir(d("5001"), d("0")));
    }

    @Test void tvaDAvoirDoitResterProportionnelle() {
        var f = facture();
        assertThrows(BusinessException.class, () -> f.appliquerAvoir(d("500"), d("101")));
    }

    @Test void remboursementSuperieurAuCreditEstRefuse() {
        var f = facture();
        f.appliquerPaiement(d("6000"));
        f.appliquerAvoir(d("500"), d("100"));
        assertThrows(BusinessException.class, () -> f.rembourser(d("601")));
    }
}