package com.gros.modules.comptabilite.repository;

import com.gros.modules.comptabilite.entity.CompteComptable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompteComptableRepository extends JpaRepository<CompteComptable, Long> {
    List<CompteComptable> findAllByOrderByNumeroAsc();
}