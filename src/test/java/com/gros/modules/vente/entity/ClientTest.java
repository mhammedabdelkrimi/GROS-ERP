package com.gros.modules.client.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientTest {
    @Test void creditAccepteSousLaLimite() {
        var c = new Client();
        c.setLimiteCredit(new BigDecimal("5000"));
        assertTrue(c.peutAcheterACredit(new BigDecimal("540"), new BigDecimal("4000")));
    }
    @Test void creditRefuseAuDessusDeLaLimite() {
        var c = new Client();
        c.setLimiteCredit(new BigDecimal("5000"));
        assertFalse(c.peutAcheterACredit(new BigDecimal("540"), new BigDecimal("5000")));
    }
    @Test void clientBloqueNePeutPasAcheter() {
        var c = new Client();
        c.setLimiteCredit(new BigDecimal("5000"));
        c.bloquer();
        assertFalse(c.peutAcheterACredit(BigDecimal.ZERO, new BigDecimal("10")));
    }
}