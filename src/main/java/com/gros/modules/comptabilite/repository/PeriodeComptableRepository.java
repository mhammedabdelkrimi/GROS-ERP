package com.gros.modules.comptabilite.repository;

import com.gros.modules.comptabilite.entity.PeriodeComptable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PeriodeComptableRepository extends JpaRepository<PeriodeComptable, Long> {
    boolean existsByAnneeAndMois(int annee, int mois);

    Optional<PeriodeComptable> findByAnneeAndMois(int annee, int mois);

    List<PeriodeComptable> findAllByOrderByAnneeDescMoisDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PeriodeComptable p where p.annee = :annee and p.mois = :mois")
    Optional<PeriodeComptable> findByAnneeEtMoisAvecVerrou(@Param("annee") int annee, @Param("mois") int mois);
}