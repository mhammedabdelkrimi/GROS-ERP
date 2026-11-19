package com.gros.modules.paiement.repository;

import com.gros.modules.facturation.entity.StatutFacture;
import com.gros.modules.paiement.entity.AffectationPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface AffectationPaiementRepository extends JpaRepository<AffectationPaiement, Long> {
    @Query("select coalesce(sum(a.montantAffecte), 0) from AffectationPaiement a "
            + "join FactureClient f on a.factureId = f.id "
            + "where f.clientId = :cid and f.statut <> :annulee")
    BigDecimal totalAffecte(@Param("cid") Long clientId, @Param("annulee") StatutFacture annulee);
}