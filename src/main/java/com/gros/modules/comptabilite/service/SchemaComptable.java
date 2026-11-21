package com.gros.modules.comptabilite.service;

import com.gros.common.exception.BusinessException;
import com.gros.modules.comptabilite.entity.LigneEcriture;
import com.gros.modules.paiement.entity.ModePaiement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public final class SchemaComptable {
    public static final String CLIENTS = "3421";
    public static final String TVA_FACTUREE = "4455";
    public static final String BANQUE = "5141";
    public static final String CAISSE = "5161";
    public static final String VENTES = "7111";

    private SchemaComptable() {}

    /** Débit Clients (TTC) / Crédit Ventes (HT) et TVA facturée. */
    public static List<LigneEcriture> facture(BigDecimal ttc, BigDecimal ht, BigDecimal tva) {
        List<LigneEcriture> l = new ArrayList<>();
        ajouter(l, LigneEcriture.auDebit(CLIENTS, ttc));
        ajouter(l, LigneEcriture.auCredit(VENTES, ht));
        ajouter(l, LigneEcriture.auCredit(TVA_FACTUREE, tva));
        return l;
    }

    /** Débit Caisse ou Banque / Crédit Clients. */
    public static List<LigneEcriture> paiement(ModePaiement mode, BigDecimal montant) {
        List<LigneEcriture> l = new ArrayList<>();
        ajouter(l, LigneEcriture.auDebit(tresorerie(mode), montant));
        ajouter(l, LigneEcriture.auCredit(CLIENTS, montant));
        return l;
    }

    /** Débit Ventes (HT) et TVA facturée / Crédit Clients. L'avoir est en TTC, réparti selon la facture. */
    public static List<LigneEcriture> avoir(BigDecimal montantTtc, BigDecimal ttcFacture, BigDecimal tvaFacture) {
        BigDecimal tva = ttcFacture.signum() == 0
                ? BigDecimal.ZERO
                : montantTtc.multiply(tvaFacture).divide(ttcFacture, 2, RoundingMode.HALF_UP);
        BigDecimal ht = montantTtc.subtract(tva);
        List<LigneEcriture> l = new ArrayList<>();
        ajouter(l, LigneEcriture.auDebit(VENTES, ht));
        ajouter(l, LigneEcriture.auDebit(TVA_FACTUREE, tva));
        ajouter(l, LigneEcriture.auCredit(CLIENTS, montantTtc));
        return l;
    }

    /** Débit Clients / Crédit Caisse ou Banque. */
    public static List<LigneEcriture> remboursement(ModePaiement mode, BigDecimal montant) {
        List<LigneEcriture> l = new ArrayList<>();
        ajouter(l, LigneEcriture.auDebit(CLIENTS, montant));
        ajouter(l, LigneEcriture.auCredit(tresorerie(mode), montant));
        return l;
    }

    public static void verifierEquilibre(List<LigneEcriture> lignes) {
        if (lignes.size() < 2)
            throw new BusinessException("Écriture invalide : au moins deux lignes sont nécessaires");
        BigDecimal debit = lignes.stream().map(LigneEcriture::getDebit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal credit = lignes.stream().map(LigneEcriture::getCredit).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (debit.compareTo(credit) != 0)
            throw new BusinessException("Écriture déséquilibrée : débit " + debit + " / crédit " + credit);
    }

    private static String tresorerie(ModePaiement mode) {
        return mode == ModePaiement.ESPECES ? CAISSE : BANQUE;
    }

    private static void ajouter(List<LigneEcriture> lignes, LigneEcriture l) {
        if (l.getDebit().add(l.getCredit()).signum() > 0) lignes.add(l);   // pas de ligne à zéro
    }
}