package com.gros.modules.stock.entity;

import com.gros.common.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventaireTest {

    private Inventaire inventaireAvecUneLigne(int theorique) {
        var inv = new Inventaire();
        inv.ajouterLigne(1L, theorique);
        return inv;
    }

    @Test void ecartEstCompteMoinsTheorique() {
        var inv = inventaireAvecUneLigne(100);
        inv.ligne(1L).compter(97, "Casse");
        assertEquals(-3, inv.ligne(1L).ecart());
    }

    @Test void clotureRefuseSiProduitNonCompte() {
        var inv = inventaireAvecUneLigne(100);
        assertThrows(BusinessException.class, () -> inv.cloturer(1L));
    }

    @Test void clotureRefuseSiEcartSansJustification() {
        var inv = inventaireAvecUneLigne(100);
        inv.ligne(1L).compter(97, null);
        assertThrows(BusinessException.class, () -> inv.cloturer(1L));
    }

    @Test void clotureAccepteeSansEcartMemeSansJustification() {
        var inv = inventaireAvecUneLigne(100);
        inv.ligne(1L).compter(100, null);
        inv.cloturer(1L);
        assertTrue(inv.isCloture());
    }

    @Test void inventaireClotureNonModifiable() {
        var inv = inventaireAvecUneLigne(100);
        inv.ligne(1L).compter(100, null);
        inv.cloturer(1L);
        assertThrows(BusinessException.class, inv::verifierOuvert);
    }
}