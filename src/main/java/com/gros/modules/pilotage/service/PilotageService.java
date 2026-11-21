package com.gros.modules.pilotage.service;

import com.gros.common.exception.BusinessException;
import com.gros.modules.client.entity.Client;
import com.gros.modules.client.repository.ClientRepository;
import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.facturation.entity.StatutFacture;
import com.gros.modules.facturation.repository.FactureClientRepository;
import com.gros.modules.pilotage.dto.CaAnnuelResponse;
import com.gros.modules.pilotage.dto.CaMoisResponse;
import com.gros.modules.pilotage.dto.ClientImpayeResponse;
import com.gros.modules.pilotage.dto.ClientPerformanceResponse;
import com.gros.modules.pilotage.dto.ImpayesResponse;
import com.gros.modules.pilotage.dto.ProduitInactifResponse;
import com.gros.modules.pilotage.dto.ProduitPerformanceResponse;
import com.gros.modules.pilotage.dto.StockPilotageResponse;
import com.gros.modules.pilotage.dto.SyntheseResponse;
import com.gros.modules.pilotage.repository.PilotageRepository;
import com.gros.modules.produit.entity.Produit;
import com.gros.modules.produit.repository.ProduitRepository;
import com.gros.modules.stock.dto.AlerteStockResponse;
import com.gros.modules.stock.repository.MouvementStockRepository;
import com.gros.modules.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class PilotageService {
    private static final StatutFacture ANNULEE = StatutFacture.ANNULEE;
    private static final List<StatutFacture> IMPAYEES =
            List.of(StatutFacture.EMISE, StatutFacture.PARTIELLEMENT_PAYEE);

    private final PilotageRepository repo;
    private final FactureClientRepository factures;
    private final ProduitRepository produits;
    private final ClientRepository clients;
    private final MouvementStockRepository mouvements;
    private final StockService stock;

    // ---------- Chiffre d'affaires ----------

    @Transactional(readOnly = true)
    public CaAnnuelResponse chiffreAffaires(int annee) {
        if (annee < 2000 || annee > 2100) throw new BusinessException("Année invalide : " + annee);
        LocalDate du = LocalDate.of(annee, 1, 1);
        LocalDate au = LocalDate.of(annee, 12, 31);

        Map<Integer, BigDecimal[]> ventes = new HashMap<>();
        for (Object[] r : repo.caMensuel(du, au, ANNULEE))
            ventes.put(entier(r[0]), new BigDecimal[]{dec(r[1]), dec(r[2])});          // [HT, TTC]
        Map<Integer, BigDecimal[]> avoirs = new HashMap<>();
        for (Object[] r : repo.avoirsMensuels(du.atStartOfDay(), au.plusDays(1).atStartOfDay(), ANNULEE))
            avoirs.put(entier(r[0]), new BigDecimal[]{dec(r[1]), dec(r[2])});          // [TTC, HT]

        LocalDate auj = LocalDate.now();
        List<CaMoisResponse> mois = new ArrayList<>();
        BigDecimal precedent = null;
        BigDecimal totalHt = BigDecimal.ZERO;
        BigDecimal totalTtc = BigDecimal.ZERO;
        for (int m = 1; m <= 12; m++) {
            BigDecimal[] v = ventes.getOrDefault(m, zeros());
            BigDecimal[] a = avoirs.getOrDefault(m, zeros());
            BigDecimal netHt = v[0].subtract(a[1]);
            BigDecimal netTtc = v[1].subtract(a[0]);
            boolean futur = annee > auj.getYear() || (annee == auj.getYear() && m > auj.getMonthValue());
            BigDecimal variation = futur ? null : CalculsPilotage.variationPourcent(netHt, precedent);
            mois.add(new CaMoisResponse(m, v[0], a[1], netHt, v[1], a[0], netTtc, variation));
            precedent = netHt;
            totalHt = totalHt.add(netHt);
            totalTtc = totalTtc.add(netTtc);
        }
        return new CaAnnuelResponse(annee, mois, totalHt, totalTtc);
    }

    // ---------- Produits ----------

    @Transactional(readOnly = true)
    public List<ProduitPerformanceResponse> produits(LocalDate du, LocalDate au, String tri, int limite) {
        LocalDate[] p = periode(du, au);
        Comparator<ProduitPerformanceResponse> ordre = switch (tri == null ? "ca" : tri.toLowerCase()) {
            case "quantite" -> Comparator.comparingLong(ProduitPerformanceResponse::quantiteVendue).reversed();
            case "ca" -> Comparator.comparing(ProduitPerformanceResponse::caHt).reversed();
            case "marge" -> Comparator.comparing(ProduitPerformanceResponse::marge).reversed();
            default -> throw new BusinessException("Tri invalide : " + tri + " (valeurs possibles : quantite, ca, marge)");
        };
        List<Object[]> rows = repo.ventesParProduit(p[0], p[1], ANNULEE);
        Map<Long, Produit> parId = produits.findAllById(rows.stream().map(r -> identifiant(r[0])).toList())
                .stream().collect(Collectors.toMap(Produit::getId, Function.identity()));
        return rows.stream().map(r -> {
            Long id = identifiant(r[0]);
            Produit prod = parId.get(id);
            BigDecimal ca = dec(r[2]);
            BigDecimal cout = dec(r[3]);
            BigDecimal marge = ca.subtract(cout);
            return new ProduitPerformanceResponse(id,
                    prod == null ? "?" : prod.getReference(), prod == null ? "?" : prod.getDesignation(),
                    ((Number) r[1]).longValue(), ca, cout, marge, CalculsPilotage.tauxMarge(marge, ca));
        }).sorted(ordre).limit(bornes(limite)).toList();
    }

    @Transactional(readOnly = true)
    public List<ProduitInactifResponse> produitsInactifs(LocalDate du, LocalDate au, int limite) {
        LocalDate[] p = periode(du, au);
        return repo.produitsSansVente(p[0], p[1], ANNULEE, PageRequest.of(0, bornes(limite))).stream()
                .map(prod -> new ProduitInactifResponse(prod.getId(), prod.getReference(), prod.getDesignation(),
                        mouvements.quantiteDisponible(prod.getId())))
                .toList();
    }

    // ---------- Clients ----------

    @Transactional(readOnly = true)
    public List<ClientPerformanceResponse> clients(LocalDate du, LocalDate au, String tri, int limite) {
        LocalDate[] p = periode(du, au);
        Comparator<ClientPerformanceResponse> ordre = switch (tri == null ? "ca" : tri.toLowerCase()) {
            case "ca" -> Comparator.comparing(ClientPerformanceResponse::caHt).reversed();
            case "marge" -> Comparator.comparing(ClientPerformanceResponse::marge).reversed();
            default -> throw new BusinessException("Tri invalide : " + tri + " (valeurs possibles : ca, marge)");
        };
        List<Object[]> rows = repo.ventesParClient(p[0], p[1], ANNULEE);
        Map<Long, String> noms = nomsClients(rows.stream().map(r -> identifiant(r[0])).toList());
        return rows.stream().map(r -> {
            Long id = identifiant(r[0]);
            BigDecimal ca = dec(r[2]);
            BigDecimal cout = dec(r[3]);
            BigDecimal marge = ca.subtract(cout);
            return new ClientPerformanceResponse(id, noms.getOrDefault(id, "?"), ((Number) r[1]).intValue(),
                    ca, cout, marge, CalculsPilotage.tauxMarge(marge, ca));
        }).sorted(ordre).limit(bornes(limite)).toList();
    }

    @Transactional(readOnly = true)
    public List<ClientImpayeResponse> clientsImpayes(int limite) {
        List<Object[]> rows = repo.impayesParClient(IMPAYEES);
        Map<Long, String> noms = nomsClients(rows.stream().map(r -> identifiant(r[0])).toList());
        return rows.stream()
                .map(r -> new ClientImpayeResponse(identifiant(r[0]), noms.getOrDefault(identifiant(r[0]), "?"),
                        ((Number) r[1]).intValue(), dec(r[2])))
                .filter(c -> c.montantImpaye().signum() > 0)
                .sorted(Comparator.comparing(ClientImpayeResponse::montantImpaye).reversed())
                .limit(bornes(limite)).toList();
    }

    // ---------- Impayés et stock ----------

    @Transactional(readOnly = true)
    public ImpayesResponse impayes() {
        LocalDate auj = LocalDate.now();
        List<FactureClient> echues = factures.echues(IMPAYEES, auj);
        CalculsPilotage.Tranches t = CalculsPilotage.ventiler(echues, auj);
        BigDecimal totalEchu = t.de1a30().add(t.de31a60()).add(t.plusDe60());
        BigDecimal nonEchu = dec(repo.impayesNonEchus(IMPAYEES, auj));
        return new ImpayesResponse(auj, nonEchu, dec(t.de1a30()), dec(t.de31a60()), dec(t.plusDe60()),
                dec(totalEchu), dec(nonEchu.add(totalEchu)), t.nombre());
    }

    @Transactional(readOnly = true)
    public StockPilotageResponse stock() {
        List<AlerteStockResponse> alertes = stock.alertes();
        return new StockPilotageResponse(dec(repo.valeurStock()), repo.produitsEnStock().intValue(), alertes);
    }

    // ---------- Synthèse du directeur ----------

    @Transactional(readOnly = true)
    public SyntheseResponse synthese() {
        YearMonth courant = YearMonth.now();
        YearMonth precedent = courant.minusMonths(1);
        BigDecimal caCourant = caNetHt(courant);
        BigDecimal caPrecedent = caNetHt(precedent);

        BigDecimal ca = BigDecimal.ZERO;
        BigDecimal cout = BigDecimal.ZERO;
        for (Object[] r : repo.ventesParProduit(courant.atDay(1), courant.atEndOfMonth(), ANNULEE)) {
            ca = ca.add(dec(r[2]));
            cout = cout.add(dec(r[3]));
        }
        BigDecimal marge = ca.subtract(cout);

        ImpayesResponse imp = impayes();
        StockPilotageResponse st = stock();
        return new SyntheseResponse(courant.getYear(), courant.getMonthValue(), caCourant, caPrecedent,
                CalculsPilotage.variationPourcent(caCourant, caPrecedent), marge,
                CalculsPilotage.tauxMarge(marge, ca), imp.totalImpaye(), imp.totalEchu(),
                st.valeurStock(), st.alertes().size());
    }

    // ---------- Interne ----------

    private BigDecimal caNetHt(YearMonth ym) {
        BigDecimal ht = BigDecimal.ZERO;
        for (Object[] r : repo.caMensuel(ym.atDay(1), ym.atEndOfMonth(), ANNULEE)) ht = ht.add(dec(r[1]));
        for (Object[] r : repo.avoirsMensuels(ym.atDay(1).atStartOfDay(), ym.plusMonths(1).atDay(1).atStartOfDay(), ANNULEE))
            ht = ht.subtract(dec(r[2]));
        return ht;
    }

    private Map<Long, String> nomsClients(List<Long> ids) {
        return clients.findAllById(ids).stream()
                .collect(Collectors.toMap(Client::getId, Client::getRaisonSociale));
    }

    /** Par défaut : du 1er janvier de l'année jusqu'à aujourd'hui. */
    private LocalDate[] periode(LocalDate du, LocalDate au) {
        LocalDate fin = au == null ? LocalDate.now() : au;
        LocalDate debut = du == null ? LocalDate.of(fin.getYear(), 1, 1) : du;
        if (debut.isAfter(fin)) throw new BusinessException("La date de début doit précéder la date de fin");
        return new LocalDate[]{debut, fin};
    }

    private static int bornes(int limite) { return Math.min(Math.max(limite, 1), 100); }

    private static BigDecimal[] zeros() {
        return new BigDecimal[]{BigDecimal.ZERO.setScale(2), BigDecimal.ZERO.setScale(2)};
    }

    private static BigDecimal dec(Object o) {
        BigDecimal v = o == null ? BigDecimal.ZERO : new BigDecimal(o.toString());
        return v.setScale(2, RoundingMode.HALF_UP);
    }

    private static int entier(Object o) { return ((Number) o).intValue(); }

    private static Long identifiant(Object o) { return ((Number) o).longValue(); }
}