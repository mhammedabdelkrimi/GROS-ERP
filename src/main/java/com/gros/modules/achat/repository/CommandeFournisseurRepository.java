package com.gros.modules.achat.repository;

import com.gros.modules.achat.entity.CommandeFournisseur;
import com.gros.modules.achat.entity.StatutCommandeAchat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeFournisseurRepository extends JpaRepository<CommandeFournisseur, Long> {
    Page<CommandeFournisseur> findByStatut(StatutCommandeAchat statut, Pageable pageable);
}