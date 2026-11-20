package com.gros.modules.facturation.repository;

import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.facturation.entity.StatutFacture;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FactureClientRepository extends JpaRepository<FactureClient, Long> {
    @Query("select coalesce(sum(f.montantTtc), 0) from FactureClient f "
            + "where f.clientId = :cid and f.statut <> :annulee")
    BigDecimal totalFactures(@Param("cid") Long clientId, @Param("annulee") StatutFacture annulee);

    @Query("select f from FactureClient f where f.statut in :statuts and f.dateEcheance < :date")
    List<FactureClient> echues(@Param("statuts") List<StatutFacture> statuts, @Param("date") LocalDate date);

    Page<FactureClient> findByStatut(StatutFacture statut, Pageable pageable);

    List<FactureClient> findByClientIdAndStatutNotOrderByIdAsc(Long clientId, StatutFacture statut);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from FactureClient f where f.id = :id")
    Optional<FactureClient> findByIdAvecVerrou(@Param("id") Long id);
}