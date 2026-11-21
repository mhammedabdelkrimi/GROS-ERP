package com.gros.modules.livraison.entity;

import com.gros.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TourneeTest {

    @Test void tourneeSansBonNePeutPasDemarrer() {
        assertThrows(BusinessException.class, () -> new Tournee().demarrer());
    }

    @Test void tourneeDemarreeNeSeModifiePlus() {
        var t = new Tournee();
        t.ajouterBon(1L);
        t.demarrer();
        assertEquals(StatutTournee.EN_COURS, t.getStatut());
        assertThrows(BusinessException.class, () -> t.ajouterBon(2L));
        assertThrows(BusinessException.class, () -> t.retirerBon(1L));
    }

    @Test void tourneePlanifieeNePeutPasEtreTerminee() {
        var t = new Tournee();
        t.ajouterBon(1L);
        assertThrows(BusinessException.class, t::terminer);
    }
}