package com.gros.modules.comptabilite.service;

import com.gros.modules.comptabilite.entity.LigneEcriture;
import com.gros.modules.paiement.entity.ModePaiement;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SchemaComptableTvaTest {

    private static BigDecimal d(String v) { return new BigDecimal(v); }

    @Test void liquidationSansRecuperableSoldeLe4455VersLe4456() {
        List<LigneEcriture> l = SchemaComptable.liquidationTva(d("234"), BigDecimal.ZERO);
        assertEquals(2, l.size());
        assertEquals("4455", l.get(0).getCompteNumero());
        assertEquals(0, d("234").compareTo(l.get(0).getDebit()));
        assertEquals("4456", l.get(1).getCompteNumero());
        assertEquals(0, d("234").compareTo(l.get(1).getCredit()));
    }

    @Test void liquidationAvecRecuperableEstEquilibree() {
        List<LigneEcriture> l = SchemaComptable.liquidationTva(d("1000"), d("300"));
        assertEquals(3, l.size());
        assertEquals("4456", l.get(2).getCompteNumero());
        assertEquals(0, d("700").compareTo(l.get(2).getCredit()));
        assertDoesNotThrow(() -> SchemaComptable.verifierEquilibre(l));
    }

    @Test void creditDeTvaVaAuCompte3456() {
        List<LigneEcriture> l = SchemaComptable.liquidationTva(d("100"), d("300"));
        assertEquals(3, l.size());
        assertEquals("3456", l.get(2).getCompteNumero());
        assertEquals(0, d("200").compareTo(l.get(2).getDebit()));
        assertDoesNotThrow(() -> SchemaComptable.verifierEquilibre(l));
    }

    @Test void avoirFournisseurDebite4411EtCreditAchatsEtTva() {
        List<LigneEcriture> l = SchemaComptable.avoirFournisseur(d("500"), d("100"), d("600"));
        assertEquals("4411", l.get(0).getCompteNumero());
        assertEquals("6111", l.get(1).getCompteNumero());
        assertEquals("3455", l.get(2).getCompteNumero());
        assertDoesNotThrow(() -> SchemaComptable.verifierEquilibre(l));
    }

    @Test void reglementDeTvaVaDu4456VersLaBanque() {
        List<LigneEcriture> l = SchemaComptable.reglementTva(ModePaiement.VIREMENT, d("500"));
        assertEquals("4456", l.get(0).getCompteNumero());
        assertEquals("5141", l.get(1).getCompteNumero());
        assertDoesNotThrow(() -> SchemaComptable.verifierEquilibre(l));
    }
}