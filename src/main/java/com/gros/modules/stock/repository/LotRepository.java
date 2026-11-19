package com.gros.modules.stock.repository;

import com.gros.modules.stock.entity.Lot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LotRepository extends JpaRepository<Lot, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)   // deux ventes ne peuvent pas prendre le dernier article
    @Query("select l from Lot l where l.produitId = :pid and l.quantiteRestante > 0")
    List<Lot> disponiblesAvecVerrou(@Param("pid") Long produitId);
}