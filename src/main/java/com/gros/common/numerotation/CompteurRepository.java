package com.gros.common.numerotation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

public interface CompteurRepository extends JpaRepository<Compteur, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)   // = SELECT ... FOR UPDATE
    Optional<Compteur> findByTypeDocAndAnnee(String typeDoc, int annee);
}