package com.gros.modules.client.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientDeblocageTest {

    @Test void deblocageDebuteUneperiodeDeGrace() {
        var c = new Client();
        c.bloquer();
        c.debloquer();
        assertFalse(c.isBloque());
        assertTrue(c.estEnGrace(7, LocalDate.now()));
    }

    @Test void pasDeGraceSansDeblocageOuApresLeDelai() {
        var c = new Client();
        assertFalse(c.estEnGrace(7, LocalDate.now()));
        c.setDateDeblocage(LocalDate.now().minusDays(7));
        assertFalse(c.estEnGrace(7, LocalDate.now()));
    }
}