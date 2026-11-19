package com.gros.modules.stock.repository;

import com.gros.modules.stock.entity.MouvementStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
    @Query("select coalesce(sum(m.quantite), 0) from MouvementStock m where m.produitId = :pid")
    long quantiteDisponible(@Param("pid") Long produitId);
}