package com.gros.modules.livraison.repository;

import com.gros.modules.livraison.entity.BonLivraison;
import com.gros.modules.livraison.entity.StatutBon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BonLivraisonRepository extends JpaRepository<BonLivraison, Long> {
    Page<BonLivraison> findByStatut(StatutBon statut, Pageable pageable);
}