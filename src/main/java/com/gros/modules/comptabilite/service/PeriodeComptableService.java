package com.gros.modules.comptabilite.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.modules.comptabilite.dto.PeriodeResponse;
import com.gros.modules.comptabilite.dto.TotauxCompte;
import com.gros.modules.comptabilite.dto.TvaResponse;
import com.gros.modules.comptabilite.entity.PeriodeComptable;
import com.gros.modules.comptabilite.repository.LigneEcritureRepository;
import com.gros.modules.comptabilite.repository.PeriodeComptableRepository;
import com.gros.modules.paiement.entity.ModePaiement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class PeriodeComptableService {
    private final PeriodeComptableRepository periodes;
    private final LigneEcritureRepository lignes;
    private final ComptabiliteService comptabilite;
    private final AuditService audit;

    /** Période ouverte : calcul en direct. Période clôturée : valeurs figées à la clôture. */
    @Transactional(readOnly = true)
    public TvaResponse calculerTva(int annee, int mois) {
        YearMonth ym = periode(annee, mois);
        Optional<PeriodeComptable> figee = periodes.findByAnneeAndMois(annee, mois);
        if (figee.isPresent()) {
            PeriodeComptable p = figee.get();
            return new TvaResponse(annee, mois, ym.atDay(1), ym.atEndOfMonth(),
                    p.getTvaFacturee(), p.getTvaRecuperable(), p.getTvaDue(), true);
        }
        return CalculTva.calculer(ym, totaux(ym), false);
    }

    /** Clôture : contrôle d'équilibre, écriture de liquidation de la TVA, TVA figée, plus aucune écriture dans le mois. */
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
        comptabilite.liquiderTva(ym, tva.tvaFacturee(), tva.tvaRecuperable());   // avant de fermer la période
        PeriodeComptable p = periodes.save(new PeriodeComptable(
                annee, mois, tva.tvaFacturee(), tva.tvaRecuperable(), tva.tvaDue()));
        audit.tracer("CLOTURE", "PERIODE_COMPTABLE", p.getId());
        return tva;
    }

    /** Paiement de la TVA due à l'administration fiscale (virement ou chèque), une seule fois par période. */
    @Transactional
    public PeriodeResponse reglerTva(int annee, int mois, ModePaiement mode) {
        YearMonth ym = periode(annee, mois);
        if (mode == ModePaiement.ESPECES)
            throw new BusinessException("Le règlement de la TVA se fait par virement ou par chèque");
        PeriodeComptable p = periodes.findByAnneeEtMoisAvecVerrou(annee, mois)
                .orElseThrow(() -> new BusinessException("La période " + ym + " n'est pas clôturée : TVA non figée"));
        if (p.getTvaDue().signum() <= 0)
            throw new BusinessException("Aucune TVA à payer pour " + ym + " (TVA due : " + p.getTvaDue() + ")");
        if (p.getTvaReglee().signum() > 0)
            throw new BusinessException("TVA déjà réglée pour " + ym);
        comptabilite.reglerTva(ym, p.getTvaDue(), mode);
        p.setTvaReglee(p.getTvaDue());
        p.setDateReglement(LocalDateTime.now());
        audit.tracer("REGLEMENT_TVA", "PERIODE_COMPTABLE", p.getId());
        return versReponse(p);
    }

    @Transactional(readOnly = true)
    public List<PeriodeResponse> lister() {
        return periodes.findAllByOrderByAnneeDescMoisDesc().stream().map(this::versReponse).toList();
    }

    private PeriodeResponse versReponse(PeriodeComptable p) {
        return new PeriodeResponse(p.getAnnee(), p.getMois(), p.getTvaFacturee(), p.getTvaRecuperable(),
                p.getTvaDue(), p.getTvaReglee(), p.getDateCloture(), p.getDateReglement());
    }

    private List<TotauxCompte> totaux(YearMonth ym) {
        return lignes.totauxParCompteHorsPiece(ym.atDay(1), ym.atEndOfMonth(), ComptabiliteService.PIECE_LIQUIDATION_TVA);
    }

    private YearMonth periode(int annee, int mois) {
        if (mois < 1 || mois > 12) throw new BusinessException("Mois invalide : " + mois + " (entre 1 et 12)");
        if (annee < 2000 || annee > 2100) throw new BusinessException("Année invalide : " + annee);
        return YearMonth.of(annee, mois);
    }
}