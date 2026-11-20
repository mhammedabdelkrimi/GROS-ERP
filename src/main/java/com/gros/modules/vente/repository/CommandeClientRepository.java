package com.gros.modules.vente.repository;

import com.gros.modules.vente.entity.CommandeClient;
import com.gros.modules.vente.entity.StatutCommande;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeClientRepository extends JpaRepository<CommandeClient, Long> {
    Page<CommandeClient> findByStatut(StatutCommande statut, Pageable pageable);
}