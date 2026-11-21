package com.gros.modules.comptabilite.repository;

import com.gros.modules.comptabilite.entity.EcritureComptable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface EcritureComptableRepository extends JpaRepository<EcritureComptable, Long> {
    Page<EcritureComptable> findByDateEcritureBetween(LocalDate du, LocalDate au, Pageable pageable);

    boolean existsByExtourneDe(Long ecritureId);
}