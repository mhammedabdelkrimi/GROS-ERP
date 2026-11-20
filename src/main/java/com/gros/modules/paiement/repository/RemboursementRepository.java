package com.gros.modules.paiement.repository;

import com.gros.modules.paiement.entity.Remboursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {
    @Query("select coalesce(sum(r.montant), 0) from Remboursement r where r.clientId = :cid")
    BigDecimal totalRembourse(@Param("cid") Long clientId);
}