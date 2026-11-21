package com.gros.modules.comptabilite.service;

import com.gros.modules.comptabilite.entity.LigneEcriture;
import com.gros.modules.paiement.entity.ModePaiement;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SchemaComptableAchatTest {

    @Test void factureFournisseurDebiteAchatsEtTvaRecuperable() {
        List<LigneEcriture> l = SchemaComptable.factureFournisseur(
                new BigDecimal("5000"), new BigDecimal("1000"), new BigDecimal("6000"));
        assertEquals(3, l.size());
        assertEquals("6111", l.get(0).getCompteNumero());
        assertEquals("3455", l.get(1).getCompteNumero());
        assertEquals("4411", l.get(2).getCompteNumero());
        assertDoesNotThrow(() -> SchemaComptable.verifierEquilibre(l));
    }

    @Test void paiementFournisseurSoldeLeCompte4411() {
        List<LigneEcriture> l = SchemaComptable.paiementFournisseur(ModePaiement.VIREMENT, new BigDecimal("2000"));
        assertEquals("4411", l.get(0).getCompteNumero());
        assertEquals("5141", l.get(1).getCompteNumero());
        assertDoesNotThrow(() -> SchemaComptable.verifierEquilibre(l));
    }
}