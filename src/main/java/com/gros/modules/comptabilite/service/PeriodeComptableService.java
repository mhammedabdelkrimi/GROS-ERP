package com.gros.modules.comptabilite.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.modules.comptabilite.dto.PeriodeResponse;
import com.gros.modules.comptabilite.dto.TotauxCompte;
import com.gros.modules.comptabilite.dto.TvaResponse;
import com.gros.modules.comptabilite.entity.PeriodeComptable;
import com.gros.modules.comptabilite.repository.LigneEcritureRepository;
import com.gros.modules.comptabilite.repository.PeriodeComptableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service @RequiredArgsConstructor
public class PeriodeComptableService {
    private final PeriodeComptableRepository periodes;
    private final LigneEcritureRepository lignes;
    private final AuditService audit;

    @Transactional(readOnly = true)
    public TvaResponse calculerTva(int annee, int mois) {
        YearMonth ym = periode(annee, mois);
        return CalculTva.calculer(ym, totaux(ym), periodes.existsByAnneeAndMois(annee, mois));
    }

    /** Clôture : contrôle d'équilibre, TVA figée, plus aucune écriture possible dans le mois. */
    @Transactional
    public TvaResponse cloturer(int annee, int mois) {
        YearMonth ym = periode(annee, mois);
        if (ym.isAfter(YearMonth.now()))
            throw new BusinessException("Impossible de clôturer une période future : " + ym);
        if (periodes.existsByAnneeAndMois(annee, mois))
            throw new BusinessException("Période déjà clôturée : " + ym);

        List<TotauxCompte> totaux = totaux(ym);
        BigDecimal debit = totaux.stream().map(TotauxCompte::debit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal credit = totaux.stream().map(TotauxCompte::credit).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (debit.compareTo(credit) != 0)
            throw new BusinessException("Période déséquilibrée : débit " + debit + " / crédit " + credit);

        TvaResponse tva = CalculTva.calculer(ym, totaux, true);
        PeriodeComptable p = periodes.save(new PeriodeComptable(
                annee, mois, tva.tvaFacturee(), tva.tvaRecuperable(), tva.tvaDue()));
        audit.tracer("CLOTURE", "PERIODE_COMPTABLE", p.getId());
        return tva;
    }

    @Transactional(readOnly = true)
    public List<PeriodeResponse> lister() {
        return periodes.findAllByOrderByAnneeDescMoisDesc().stream()
                .map(p -> new PeriodeResponse(p.getAnnee(), p.getMois(), p.getTvaFacturee(),
                        p.getTvaRecuperable(), p.getTvaDue(), p.getDateCloture()))
                .toList();
    }

    private List<TotauxCompte> totaux(YearMonth ym) {
        return lignes.totauxParCompte(ym.atDay(1), ym.atEndOfMonth());
    }

    private YearMonth periode(int annee, int mois) {
        if (mois < 1 || mois > 12) throw new BusinessException("Mois invalide : " + mois + " (entre 1 et 12)");
        if (annee < 2000 || annee > 2100) throw new BusinessException("Année invalide : " + annee);
        return YearMonth.of(annee, mois);
    }
}