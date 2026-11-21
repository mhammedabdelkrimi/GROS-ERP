package com.gros.modules.achat.repository;

import com.gros.modules.achat.entity.FactureFournisseur;
import com.gros.modules.achat.entity.StatutFactureFournisseur;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface FactureFournisseurRepository extends JpaRepository<FactureFournisseur, Long> {
    boolean existsByFournisseurIdAndNumeroFournisseur(Long fournisseurId, String numeroFournisseur);

    Page<FactureFournisseur> findByStatut(StatutFactureFournisseur statut, Pageable pageable);

    @Query("select coalesce(sum(f.montantHt - f.montantAvoirHt), 0) from FactureFournisseur f where f.commandeId = :cid")
    BigDecimal totalHtParCommande(@Param("cid") Long commandeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from FactureFournisseur f where f.id = :id")
    Optional<FactureFournisseur> findByIdAvecVerrou(@Param("id") Long id);
}