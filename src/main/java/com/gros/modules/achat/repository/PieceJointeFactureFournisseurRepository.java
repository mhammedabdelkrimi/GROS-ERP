package com.gros.modules.achat.repository;

import com.gros.modules.achat.entity.PieceJointeFactureFournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PieceJointeFactureFournisseurRepository extends JpaRepository<PieceJointeFactureFournisseur, Long> {
    boolean existsByFactureId(Long factureId);

    Optional<PieceJointeFactureFournisseur> findByFactureId(Long factureId);
}