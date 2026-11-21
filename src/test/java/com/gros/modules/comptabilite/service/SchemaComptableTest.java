package com.gros.modules.comptabilite.service;

import com.gros.common.exception.BusinessException;
import com.gros.modules.comptabilite.entity.LigneEcriture;
import com.gros.modules.paiement.entity.ModePaiement;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SchemaComptableTest {

    @Test void factureEstEquilibreeSurTroisLignes() {
        List<LigneEcriture> l = SchemaComptable.facture(new BigDecimal("780"), new BigDecimal("650"), new BigDecimal("130"));
        assertEquals(3, l.size());
        assertDoesNotThrow(() -> SchemaComptable.verifierEquilibre(l));
    }

    @Test void factureSansTvaN_aPasDeLigneTva() {
        List<LigneEcriture> l = SchemaComptable.facture(new BigDecimal("100"), new BigDecimal("100"), BigDecimal.ZERO);
        assertEquals(2, l.size());
    }

    @Test void avoirRepartitHtEtTvaSelonLaFacture() {
        List<LigneEcriture> l = SchemaComptable.avoir(new BigDecimal("156"), new BigDecimal("780"), new BigDecimal("130"));
        assertEquals(3, l.size());
        assertEquals("7111", l.get(0).getCompteNumero());
        assertEquals(0, new BigDecimal("130").compareTo(l.get(0).getDebit()));
        assertEquals(0, new BigDecimal("26").compareTo(l.get(1).getDebit()));
        assertDoesNotThrow(() -> SchemaComptable.verifierEquilibre(l));
    }

    @Test void especesVaEnCaisseEtVirementEnBanque() {
        assertEquals("5161", SchemaComptable.paiement(ModePaiement.ESPECES, BigDecimal.TEN).get(0).getCompteNumero());
        assertEquals("5141", SchemaComptable.paiement(ModePaiement.VIREMENT, BigDecimal.TEN).get(0).getCompteNumero());
        assertEquals("5141", SchemaComptable.remboursement(ModePaiement.CHEQUE, BigDecimal.TEN).get(1).getCompteNumero());
    }

    @Test void ecritureDesequilibreeEstRefusee() {
        List<LigneEcriture> l = SchemaComptable.facture(new BigDecimal("780"), new BigDecimal("650"), new BigDecimal("100"));
        assertThrows(BusinessException.class, () -> SchemaComptable.verifierEquilibre(l));
    }
}