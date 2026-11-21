package com.gros.modules.stock.repository;

import com.gros.modules.stock.entity.Inventaire;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventaireRepository extends JpaRepository<Inventaire, Long> {
    boolean existsByClotureFalse();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventaire i where i.id = :id")
    Optional<Inventaire> findByIdAvecVerrou(@Param("id") Long id);
}