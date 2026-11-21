package com.gros.modules.comptabilite.repository;

import com.gros.modules.comptabilite.dto.TotauxCompte;
import com.gros.modules.comptabilite.entity.LigneEcriture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface LigneEcritureRepository extends JpaRepository<LigneEcriture, Long> {
    List<LigneEcriture> findByEcritureId(Long ecritureId);

    List<LigneEcriture> findByEcritureIdIn(Collection<Long> ecritureIds);

    @Query("select new com.gros.modules.comptabilite.dto.TotauxCompte(l.compteNumero, sum(l.debit), sum(l.credit)) "
            + "from LigneEcriture l join EcritureComptable e on l.ecritureId = e.id "
            + "where e.dateEcriture between :du and :au "
            + "group by l.compteNumero order by l.compteNumero")
    List<TotauxCompte> totauxParCompte(@Param("du") LocalDate du, @Param("au") LocalDate au);

    /** Totaux sans un type de pièce : sert au calcul de la TVA, qui ne doit pas compter sa propre liquidation. */
    @Query("select new com.gros.modules.comptabilite.dto.TotauxCompte(l.compteNumero, sum(l.debit), sum(l.credit)) "
            + "from LigneEcriture l join EcritureComptable e on l.ecritureId = e.id "
            + "where e.dateEcriture between :du and :au and e.pieceType <> :piece "
            + "group by l.compteNumero order by l.compteNumero")
    List<TotauxCompte> totauxParCompteHorsPiece(@Param("du") LocalDate du, @Param("au") LocalDate au,
                                                @Param("piece") String pieceType);
}