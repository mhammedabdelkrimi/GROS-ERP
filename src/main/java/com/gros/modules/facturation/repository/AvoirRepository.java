package com.gros.modules.facturation.repository;

import com.gros.modules.facturation.entity.Avoir;
import com.gros.modules.facturation.entity.StatutFacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface AvoirRepository extends JpaRepository<Avoir, Long> {
    @Query("select coalesce(sum(a.montant), 0) from Avoir a join FactureClient f on a.factureId = f.id "
            + "where f.clientId = :cid and f.statut <> :annulee")
    BigDecimal totalAvoirs(@Param("cid") Long clientId, @Param("annulee") StatutFacture annulee);
}