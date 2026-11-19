package com.gros.modules.paiement.repository;

import com.gros.modules.paiement.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {}