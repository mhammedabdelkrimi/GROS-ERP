package com.gros.modules.comptabilite.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.common.numerotation.NumerotationService;
import com.gros.modules.comptabilite.dto.BalanceLigneResponse;
import com.gros.modules.comptabilite.dto.BalanceResponse;
import com.gros.modules.comptabilite.dto.CompteResponse;
import com.gros.modules.comptabilite.dto.EcritureResponse;
import com.gros.modules.comptabilite.dto.LigneEcritureResponse;
import com.gros.modules.comptabilite.entity.CompteComptable;
import com.gros.modules.comptabilite.entity.EcritureComptable;
import com.gros.modules.comptabilite.entity.LigneEcriture;
import com.gros.modules.comptabilite.repository.CompteComptableRepository;
import com.gros.modules.comptabilite.repository.EcritureComptableRepository;
import com.gros.modules.comptabilite.repository.LigneEcritureRepository;
import com.gros.modules.comptabilite.repository.PeriodeComptableRepository;
import com.gros.modules.facturation.entity.Avoir;
import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.paiement.entity.ModePaiement;
import com.gros.modules.paiement.entity.Paiement;
import com.gros.modules.paiement.entity.Remboursement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class ComptabiliteService {
    private static final LocalDate DEBUT = LocalDate.of(1900, 1, 1);
    private static final LocalDate FIN = LocalDate.of(2999, 12, 31);
    public static final String PIECE_LIQUIDATION_TVA = "LIQUIDATION_TVA";

    private final EcritureComptableRepository ecritures;
    private final LigneEcritureRepository lignes;
    private final CompteComptableRepository comptes;
    private final PeriodeComptableRepository periodes;
    private final NumerotationService numerotation;
    private final AuditService audit;

    // ---------- Écritures automatiques (appelées depuis la transaction de l'opération) ----------

    @Transactional
    public void comptabiliserFacture(FactureClient f) {
        poster("Facture client " + f.getNumero(), "FACTURE_CLIENT", f.getId(), null,
                SchemaComptable.facture(f.getMontantTtc(), f.getMontantHt(), f.getMontantTva()));
    }

    @Transactional
    public void comptabiliserPaiement(Paiement p) {
        poster("Paiement client " + p.getClientId() + " (" + p.getModePaiement() + ")", "PAIEMENT", p.getId(), null,
                SchemaComptable.paiement(p.getModePaiement(), p.getMontant()));
    }

    @Transactional
    public void comptabiliserAvoir(Avoir a, FactureClient f) {
        poster("Avoir " + a.getNumero() + " sur facture " + f.getNumero(), "AVOIR", a.getId(), null,
                SchemaComptable.avoir(a.getMontant(), f.getMontantTtc(), f.getMontantTva()));
    }

    @Transactional
    public void comptabiliserRemboursement(Remboursement r) {
        poster("Remboursement client " + r.getClientId() + " (facture " + r.getFactureId() + ")",
                "REMBOURSEMENT", r.getId(), null,
                SchemaComptable.remboursement(r.getModePaiement(), r.getMontant()));
    }

    @Transactional
    public void comptabiliserFactureFournisseur(String numero, Long factureId,
                                                BigDecimal ht, BigDecimal tva, BigDecimal ttc) {
        poster("Facture fournisseur " + numero, "FACTURE_FOURNISSEUR", factureId, null,
                SchemaComptable.factureFournisseur(ht, tva, ttc));
    }

    @Transactional
    public void comptabiliserPaiementFournisseur(Long paiementId, Long factureId,
                                                 ModePaiement mode, BigDecimal montant) {
        poster("Paiement fournisseur (facture " + factureId + ", " + mode + ")",
                "PAIEMENT_FOURNISSEUR", paiementId, null,
                SchemaComptable.paiementFournisseur(mode, montant));
    }

    @Transactional
    public void comptabiliserAvoirFournisseur(String numero, Long avoirId,
                                              BigDecimal ht, BigDecimal tva, BigDecimal ttc) {
        poster("Avoir fournisseur " + numero, "AVOIR_FOURNISSEUR", avoirId, null,
                SchemaComptable.avoirFournisseur(ht, tva, ttc));
    }

    @Transactional
    public void comptabiliserRemboursementFournisseur(Long factureId, ModePaiement mode, BigDecimal montant) {
        poster("Remboursement fournisseur (facture " + factureId + ", " + mode + ")",
                "REMBOURSEMENT_FOURNISSEUR", factureId, null,
                SchemaComptable.remboursementFournisseur(mode, montant));
    }

    /** Écriture de liquidation de la TVA, datée de la fin du mois (ou d'aujourd'hui si le mois n'est pas fini). */
    @Transactional
    public void liquiderTva(YearMonth ym, BigDecimal facturee, BigDecimal recuperable) {
        if (facturee.signum() == 0 && recuperable.signum() == 0) return;     // rien à liquider
        LocalDate fin = ym.atEndOfMonth();
        LocalDate date = fin.isAfter(LocalDate.now()) ? LocalDate.now() : fin;
        poster("Liquidation de la TVA " + ym, PIECE_LIQUIDATION_TVA, pieceId(ym), null, date,
                SchemaComptable.liquidationTva(facturee, recuperable));
    }

    @Transactional
    public void reglerTva(YearMonth ym, BigDecimal montant, ModePaiement mode) {
        poster("Règlement de la TVA " + ym + " (" + mode + ")", "REGLEMENT_TVA", pieceId(ym), null,
                SchemaComptable.reglementTva(mode, montant));
    }

    // ---------- Extourne ----------

    @Transactional
    public EcritureResponse extourner(Long id, String motif) {
        EcritureComptable origine = ecritures.findById(id)
                .orElseThrow(() -> new NotFoundException("Écriture introuvable : " + id));
        if (origine.getExtourneDe() != null)
            throw new BusinessException("Une écriture d'extourne ne peut pas être extournée");
        if (PIECE_LIQUIDATION_TVA.equals(origine.getPieceType()))
            throw new BusinessException("La liquidation de TVA d'une période clôturée ne peut pas être extournée");
        if (ecritures.existsByExtourneDe(id))
            throw new BusinessException("Écriture déjà extournée");
        List<LigneEcriture> inverse = lignes.findByEcritureId(id).stream().map(LigneEcriture::inverse).toList();
        EcritureComptable e = poster("Extourne de " + origine.getNumero() + " : " + motif,
                origine.getPieceType(), origine.getPieceId(), id, inverse);
        return versReponse(e, inverse, intitules());
    }

    // ---------- Consultation ----------

    @Transactional(readOnly = true)
    public List<CompteResponse> comptes() {
        return comptes.findAllByOrderByNumeroAsc().stream()
                .map(c -> new CompteResponse(c.getNumero(), c.getIntitule())).toList();
    }

    @Transactional(readOnly = true)
    public Page<EcritureResponse> journal(LocalDate du, LocalDate au, Pageable pageable) {
        verifierPeriode(du, au);
        Page<EcritureComptable> page = ecritures.findByDateEcritureBetween(
                du == null ? DEBUT : du, au == null ? FIN : au, pageable);
        List<Long> ids = page.getContent().stream().map(EcritureComptable::getId).toList();
        Map<Long, List<LigneEcriture>> parEcriture = ids.isEmpty() ? Map.of()
                : lignes.findByEcritureIdIn(ids).stream().collect(Collectors.groupingBy(LigneEcriture::getEcritureId));
        Map<String, String> intitules = intitules();
        return page.map(e -> versReponse(e, parEcriture.getOrDefault(e.getId(), List.of()), intitules));
    }

    @Transactional(readOnly = true)
    public EcritureResponse consulter(Long id) {
        EcritureComptable e = ecritures.findById(id)
                .orElseThrow(() -> new NotFoundException("Écriture introuvable : " + id));
        return versReponse(e, lignes.findByEcritureId(id), intitules());
    }

    @Transactional(readOnly = true)
    public BalanceResponse balance(LocalDate du, LocalDate au) {
        verifierPeriode(du, au);
        Map<String, String> intitules = intitules();
        List<BalanceLigneResponse> comptesBalance = lignes
                .totauxParCompte(du == null ? DEBUT : du, au == null ? FIN : au).stream()
                .map(t -> {
                    BigDecimal solde = t.debit().subtract(t.credit());
                    return new BalanceLigneResponse(t.compteNumero(), intitules.getOrDefault(t.compteNumero(), ""),
                            t.debit(), t.credit(),
                            solde.signum() > 0 ? solde : BigDecimal.ZERO,
                            solde.signum() < 0 ? solde.negate() : BigDecimal.ZERO);
                }).toList();
        BigDecimal totalDebit = comptesBalance.stream().map(BalanceLigneResponse::totalDebit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = comptesBalance.stream().map(BalanceLigneResponse::totalCredit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new BalanceResponse(du, au, comptesBalance, totalDebit, totalCredit);
    }

    // ---------- Interne ----------

    private EcritureComptable poster(String libelle, String pieceType, Long pieceId, Long extourneDe,
                                     List<LigneEcriture> modele) {
        return poster(libelle, pieceType, pieceId, extourneDe, LocalDate.now(), modele);
    }

    private EcritureComptable poster(String libelle, String pieceType, Long pieceId, Long extourneDe,
                                     LocalDate date, List<LigneEcriture> modele) {
        verifierPeriodeOuverte(date);                     // avant la numérotation : aucun numéro consommé
        SchemaComptable.verifierEquilibre(modele);
        EcritureComptable nouvelle = new EcritureComptable(
                numerotation.prochainNumero("EC"), libelle, pieceType, pieceId, extourneDe);
        nouvelle.setDateEcriture(date);
        EcritureComptable e = ecritures.save(nouvelle);
        for (LigneEcriture l : modele) l.setEcritureId(e.getId());
        lignes.saveAll(modele);
        audit.tracer("CREATION", "ECRITURE_COMPTABLE", e.getId());
        return e;
    }

    private void verifierPeriodeOuverte(LocalDate date) {
        YearMonth ym = YearMonth.from(date);
        if (periodes.existsByAnneeAndMois(ym.getYear(), ym.getMonthValue()))
            throw new BusinessException("Période comptable clôturée : " + ym + ". Aucune écriture n'est possible.");
    }

    private void verifierPeriode(LocalDate du, LocalDate au) {
        if (du != null && au != null && du.isAfter(au))
            throw new BusinessException("La date de début doit précéder la date de fin");
    }

    private static Long pieceId(YearMonth ym) { return ym.getYear() * 100L + ym.getMonthValue(); }

    private Map<String, String> intitules() {
        return comptes.findAll().stream()
                .collect(Collectors.toMap(CompteComptable::getNumero, CompteComptable::getIntitule));
    }

    private EcritureResponse versReponse(EcritureComptable e, List<LigneEcriture> ls, Map<String, String> intitules) {
        List<LigneEcritureResponse> details = ls.stream()
                .sorted(Comparator.comparing(LigneEcriture::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(l -> new LigneEcritureResponse(l.getCompteNumero(),
                        intitules.getOrDefault(l.getCompteNumero(), ""), l.getDebit(), l.getCredit()))
                .toList();
        BigDecimal totalDebit = ls.stream().map(LigneEcriture::getDebit).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = ls.stream().map(LigneEcriture::getCredit).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new EcritureResponse(e.getId(), e.getNumero(), e.getDateEcriture(), e.getLibelle(),
                e.getPieceType(), e.getPieceId(), e.getExtourneDe(), totalDebit, totalCredit, details);
    }
}