package com.gros.modules.achat.repository;

import com.gros.modules.achat.entity.AvoirFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvoirFournisseurRepository extends JpaRepository<AvoirFournisseur, Long> {
    boolean existsByFournisseurIdAndNumeroFournisseur(Long fournisseurId, String numeroFournisseur);
}