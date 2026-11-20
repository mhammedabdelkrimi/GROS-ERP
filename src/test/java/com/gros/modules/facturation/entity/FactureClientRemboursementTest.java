package com.gros.modules.facturation.entity;

import com.gros.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FactureClientRemboursementTest {

    private FactureClient facturePayeeAvecAvoir() {
        var f = new FactureClient();
        f.setMontantTtc(new BigDecimal("780"));
        f.appliquerPaiement(new BigDecimal("780"));
        f.appliquerAvoir(new BigDecimal("156"));
        return f;
    }

    @Test void remboursementReduitLeCredit() {
        var f = facturePayeeAvecAvoir();
        f.rembourser(new BigDecimal("100"));
        assertEquals(0, new BigDecimal("56").compareTo(f.creditGenere()));
    }

    @Test void remboursementSuperieurAuCreditEstRefuse() {
        var f = facturePayeeAvecAvoir();
        assertThrows(BusinessException.class, () -> f.rembourser(new BigDecimal("157")));
    }

    @Test void factureSansCreditNePeutPasEtreRemboursee() {
        var f = new FactureClient();
        f.setMontantTtc(new BigDecimal("780"));
        f.appliquerAvoir(new BigDecimal("100"));
        assertThrows(BusinessException.class, () -> f.rembourser(new BigDecimal("10")));
    }
}