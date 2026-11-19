package com.gros.modules.vente.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LigneCommandeClientTest {
    @Test void calculeLesMontantsAvecRemise() {
        var l = new LigneCommandeClient(1L, 10, new BigDecimal("100.00"), new BigDecimal("20"));
        l.setRemisePourcent(new BigDecimal("10"));
        assertEquals(new BigDecimal("900.00"), l.montantHT());
        assertEquals(new BigDecimal("1080.00"), l.montantTTC());
    }
}