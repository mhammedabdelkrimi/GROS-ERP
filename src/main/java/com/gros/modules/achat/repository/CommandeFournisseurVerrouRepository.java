package com.gros.modules.achat.repository;

import com.gros.modules.achat.entity.CommandeFournisseur;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommandeFournisseurVerrouRepository extends Repository<CommandeFournisseur, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CommandeFournisseur c where c.id = :id")
    Optional<CommandeFournisseur> findByIdAvecVerrou(@Param("id") Long id);
}