package com.gros.modules.vente.entity;

import com.gros.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DevisTest {

    private Devis devis() {
        var d = new Devis();
        d.setDateValidite(LocalDate.now().plusDays(30));
        d.getLignes().add(new LigneDevis(1L, 10, new BigDecimal("65.00"), new BigDecimal("20")));
        d.calculerTotaux();
        return d;
    }

    @Test void calculeLesTotaux() {
        var d = devis();
        assertEquals(0, new BigDecimal("650.00").compareTo(d.getMontantHt()));
        assertEquals(0, new BigDecimal("130.00").compareTo(d.getMontantTva()));
        assertEquals(0, new BigDecimal("780.00").compareTo(d.getMontantTtc()));
    }

    @Test void brouillonNePeutPasEtreAccepteDirectement() {
        assertThrows(BusinessException.class, () -> devis().accepter());
    }

    @Test void devisAccepteEstConvertibleUneSeuleFois() {
        var d = devis();
        d.envoyer();
        d.accepter();
        d.verifierConvertible();
        d.marquerConverti(7L);
        assertEquals(StatutDevis.CONVERTI, d.getStatut());
        assertEquals(7L, d.getCommandeId());
        assertThrows(BusinessException.class, d::verifierConvertible);
    }

    @Test void devisExpireNePeutPlusEtreAccepte() {
        var d = devis();
        d.envoyer();
        d.setDateValidite(LocalDate.now().minusDays(1));
        assertThrows(BusinessException.class, d::accepter);
    }

    @Test void devisBrouillonNEstPasConvertible() {
        assertThrows(BusinessException.class, () -> devis().verifierConvertible());
    }
}