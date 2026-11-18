package com.gros.modules.produit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gros.modules.produit.entity.Produit;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    boolean existsByReference(String reference);

    // pagination + recherche (BNF-24)
    Page<Produit> findByActifTrueAndDesignationContainingIgnoreCase(String q, Pageable p);
}