package com.gros.modules.livraison.repository;

import com.gros.modules.livraison.entity.Tournee;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TourneeRepository extends JpaRepository<Tournee, Long> {
    List<Tournee> findByLivreurIdAndDateTourneeOrderByIdAsc(Long livreurId, LocalDate date);

    Page<Tournee> findByDateTournee(LocalDate date, Pageable pageable);

    /** Nombre de tournées qui contiennent déjà ce bon (0 ou 1). */
    @Query("select count(t) from Tournee t join t.bonIds b where b = :bonId")
    long compterBon(@Param("bonId") Long bonId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tournee t where t.id = :id")
    Optional<Tournee> findByIdAvecVerrou(@Param("id") Long id);
}