package com.gros.modules.vente.repository;

import com.gros.modules.vente.entity.CommandeClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeClientRepository extends JpaRepository<CommandeClient, Long> {}