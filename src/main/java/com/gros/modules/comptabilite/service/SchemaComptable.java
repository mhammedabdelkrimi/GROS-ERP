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
    public static final String TVA_RECUPERABLE = "3455";
    public static final String CREDIT_TVA = "3456";
    public static final String FOURNISSEURS = "4411";
    public static final String TVA_FACTUREE = "4455";
    public static final String TVA_DUE = "4456";
    public static final String BANQUE = "5141";
    public static final String CAISSE = "5161";
    public static final String ACHATS = "6111";
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

    /** Débit Achats (HT) et TVA récupérable / Crédit Fournisseurs (TTC). */
    public static List<LigneEcriture> factureFournisseur(BigDecimal ht, BigDecimal tva, BigDecimal ttc) {
        List<LigneEcriture> l = new ArrayList<>();
        ajouter(l, LigneEcriture.auDebit(ACHATS, ht));
        ajouter(l, LigneEcriture.auDebit(TVA_RECUPERABLE, tva));
        ajouter(l, LigneEcriture.auCredit(FOURNISSEURS, ttc));
        return l;
    }

    /** Débit Fournisseurs / Crédit Caisse ou Banque. */
    public static List<LigneEcriture> paiementFournisseur(ModePaiement mode, BigDecimal montant) {
        List<LigneEcriture> l = new ArrayList<>();
        ajouter(l, LigneEcriture.auDebit(FOURNISSEURS, montant));
        ajouter(l, LigneEcriture.auCredit(tresorerie(mode), montant));
        return l;
    }

    /** Avoir fournisseur : Débit Fournisseurs (TTC) / Crédit Achats (HT) et TVA récupérable. */
    public static List<LigneEcriture> avoirFournisseur(BigDecimal ht, BigDecimal tva, BigDecimal ttc) {
        List<LigneEcriture> l = new ArrayList<>();
        ajouter(l, LigneEcriture.auDebit(FOURNISSEURS, ttc));
        ajouter(l, LigneEcriture.auCredit(ACHATS, ht));
        ajouter(l, LigneEcriture.auCredit(TVA_RECUPERABLE, tva));
        return l;
    }

    /** Le fournisseur rembourse un trop-payé : Débit Caisse ou Banque / Crédit Fournisseurs. */
    public static List<LigneEcriture> remboursementFournisseur(ModePaiement mode, BigDecimal montant) {
        List<LigneEcriture> l = new ArrayList<>();
        ajouter(l, LigneEcriture.auDebit(tresorerie(mode), montant));
        ajouter(l, LigneEcriture.auCredit(FOURNISSEURS, montant));
        return l;
    }

    /**
     * Liquidation de la TVA d'une période : solde 4455 et 3455.
     * TVA due positive : Crédit 4456. TVA due négative : Débit 3456 (crédit de TVA).
     */
    public static List<LigneEcriture> liquidationTva(BigDecimal facturee, BigDecimal recuperable) {
        BigDecimal due = facturee.subtract(recuperable);
        List<LigneEcriture> l = new ArrayList<>();
        signe(l, TVA_FACTUREE, facturee);                 // positif : débit pour solder
        signe(l, TVA_RECUPERABLE, recuperable.negate());  // positif : crédit pour solder
        signe(l, due.signum() >= 0 ? TVA_DUE : CREDIT_TVA, due.negate());
        return l;
    }

    /** Règlement de la TVA due à l'administration : Débit 4456 / Crédit Banque. */
    public static List<LigneEcriture> reglementTva(ModePaiement mode, BigDecimal montant) {
        List<LigneEcriture> l = new ArrayList<>();
        ajouter(l, LigneEcriture.auDebit(TVA_DUE, montant));
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

    /** netDebit > 0 : ligne au débit. netDebit < 0 : ligne au crédit. 0 : rien. */
    private static void signe(List<LigneEcriture> lignes, String compte, BigDecimal netDebit) {
        if (netDebit.signum() > 0) lignes.add(LigneEcriture.auDebit(compte, netDebit));
        else if (netDebit.signum() < 0) lignes.add(LigneEcriture.auCredit(compte, netDebit.negate()));
    }
}