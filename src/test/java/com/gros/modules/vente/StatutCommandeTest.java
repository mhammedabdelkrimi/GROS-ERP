package com.gros.modules.vente;

import com.gros.modules.vente.entity.StatutCommande;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatutCommandeTest {
    @Test void brouillonPeutEtreValide() {
        assertTrue(StatutCommande.BROUILLON.peutPasserA(StatutCommande.VALIDEE));
    }
    @Test void livreeNePeutPasRevenirEnBrouillon() {
        assertFalse(StatutCommande.LIVREE.peutPasserA(StatutCommande.BROUILLON));
    }
    @Test void commandeAnnuleeEstDefinitive() {
        assertFalse(StatutCommande.ANNULEE.peutPasserA(StatutCommande.VALIDEE));
    }
}