package com.gros.modules.pilotage.repository;

import com.gros.modules.facturation.entity.LigneFacture;
import com.gros.modules.facturation.entity.StatutFacture;
import com.gros.modules.produit.entity.Produit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Requêtes de lecture uniquement. Chaque ligne de résultat est un Object[] converti par PilotageService. */
public interface PilotageRepository extends Repository<LigneFacture, Long> {

    /** [mois, HT, TTC] des factures non annulées. */
    @Query("select month(f.dateEmission), sum(f.montantHt), sum(f.montantTtc) from FactureClient f "
            + "where f.dateEmission between :du and :au and f.statut <> :annulee "
            + "group by month(f.dateEmission)")
    List<Object[]> caMensuel(@Param("du") LocalDate du, @Param("au") LocalDate au,
                             @Param("annulee") StatutFacture annulee);

    /** [mois, avoir TTC, avoir HT]. L'avoir HT est réparti selon le rapport HT/TTC de la facture. */
    @Query("select month(a.dateCreation), sum(a.montant), sum(a.montant * f.montantHt / f.montantTtc) "
            + "from Avoir a join FactureClient f on a.factureId = f.id "
            + "where a.dateCreation >= :debut and a.dateCreation < :fin and f.montantTtc > 0 and f.statut <> :annulee "
            + "group by month(a.dateCreation)")
    List<Object[]> avoirsMensuels(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin,
                                  @Param("annulee") StatutFacture annulee);

    /** [produitId, quantité vendue, CA HT, coût]. */
    @Query("select l.produitId, sum(l.quantite), "
            + "sum(l.prixUnitaire * l.quantite * (100 - l.remisePourcent) / 100), "
            + "sum(l.coutUnitaire * l.quantite) "
            + "from FactureClient f join f.lignes l "
            + "where f.dateEmission between :du and :au and f.statut <> :annulee "
            + "group by l.produitId")
    List<Object[]> ventesParProduit(@Param("du") LocalDate du, @Param("au") LocalDate au,
                                    @Param("annulee") StatutFacture annulee);

    /** [clientId, nombre de factures, CA HT, coût]. */
    @Query("select f.clientId, count(distinct f.id), "
            + "sum(l.prixUnitaire * l.quantite * (100 - l.remisePourcent) / 100), "
            + "sum(l.coutUnitaire * l.quantite) "
            + "from FactureClient f join f.lignes l "
            + "where f.dateEmission between :du and :au and f.statut <> :annulee "
            + "group by f.clientId")
    List<Object[]> ventesParClient(@Param("du") LocalDate du, @Param("au") LocalDate au,
                                   @Param("annulee") StatutFacture annulee);

    /** [clientId, nombre de factures, reste à payer]. */
    @Query("select f.clientId, count(f), sum(f.montantTtc - f.montantAvoir - f.montantPaye) "
            + "from FactureClient f where f.statut in :statuts group by f.clientId")
    List<Object[]> impayesParClient(@Param("statuts") List<StatutFacture> statuts);

    /** Reste à payer des factures dont l'échéance n'est pas encore passée. */
    @Query("select coalesce(sum(f.montantTtc - f.montantAvoir - f.montantPaye), 0) from FactureClient f "
            + "where f.statut in :statuts and f.dateEcheance >= :date")
    Number impayesNonEchus(@Param("statuts") List<StatutFacture> statuts, @Param("date") LocalDate date);

    /** Produits actifs sans aucune vente sur la période (« ne tournent plus »). */
    @Query("select p from Produit p where p.actif = true and p.id not in ("
            + "select l.produitId from FactureClient f join f.lignes l "
            + "where f.dateEmission between :du and :au and f.statut <> :annulee) "
            + "order by p.designation")
    List<Produit> produitsSansVente(@Param("du") LocalDate du, @Param("au") LocalDate au,
                                    @Param("annulee") StatutFacture annulee, Pageable pageable);

    /** Valeur du stock au prix d'achat actuel. */
    @Query("select coalesce(sum(l.quantiteRestante * p.prixAchat), 0) "
            + "from Lot l join Produit p on l.produitId = p.id where l.quantiteRestante > 0")
    Number valeurStock();

    @Query("select count(distinct l.produitId) from Lot l where l.quantiteRestante > 0")
    Number produitsEnStock();
}