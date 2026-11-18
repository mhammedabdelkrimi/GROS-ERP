package com.gros.modules.produit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gros.modules.produit.entity.HistoriquePrix;

public interface HistoriquePrixRepository extends JpaRepository<HistoriquePrix, Long> {

    List<HistoriquePrix> findByProduitIdOrderByDateChangementDesc(Long produitId);
}