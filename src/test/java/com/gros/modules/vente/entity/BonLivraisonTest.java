package com.gros.modules.livraison.entity;

import com.gros.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BonLivraisonTest {
    @Test void confirmationRefuseeSansSignature() {
        var bon = new BonLivraison("BL-2026-000001", 1L);
        assertThrows(BusinessException.class, bon::confirmer);
    }

    @Test void confirmationAccepteeApresSignature() {
        var bon = new BonLivraison("BL-2026-000001", 1L);
        bon.faireSigner();
        bon.confirmer();
        assertEquals(StatutBon.CONFIRME, bon.getStatut());
    }

    @Test void bonRefuseNePeutPlusEtreConfirme() {
        var bon = new BonLivraison("BL-2026-000001", 1L);
        bon.refuser("Marchandise non conforme");
        assertThrows(BusinessException.class, bon::confirmer);
    }
}