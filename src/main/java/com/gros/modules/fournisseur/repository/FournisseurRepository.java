package com.gros.modules.fournisseur.repository;

import com.gros.modules.fournisseur.entity.Fournisseur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    Page<Fournisseur> findByActifTrueAndRaisonSocialeContainingIgnoreCase(String q, Pageable p);
}