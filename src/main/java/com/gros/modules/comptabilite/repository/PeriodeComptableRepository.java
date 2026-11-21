package com.gros.modules.comptabilite.repository;

import com.gros.modules.comptabilite.entity.PeriodeComptable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeriodeComptableRepository extends JpaRepository<PeriodeComptable, Long> {
    boolean existsByAnneeAndMois(int annee, int mois);

    List<PeriodeComptable> findAllByOrderByAnneeDescMoisDesc();
}