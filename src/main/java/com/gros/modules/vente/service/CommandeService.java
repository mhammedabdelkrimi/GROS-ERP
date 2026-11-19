package com.gros.modules.vente.service;

import com.gros.common.SecurityUtils;
import com.gros.common.document.ServiceDocument;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.common.numerotation.NumerotationService;
import com.gros.common.parametre.ParametreService;
import com.gros.modules.auth.entity.Role;
import com.gros.modules.client.entity.Client;
import com.gros.modules.client.repository.ClientRepository;
import com.gros.modules.livraison.dto.BonLivraisonResponse;
import com.gros.modules.livraison.entity.BonLivraison;
import com.gros.modules.livraison.repository.BonLivraisonRepository;
import com.gros.modules.paiement.service.SoldeClientService;
import com.gros.modules.produit.entity.Produit;
import com.gros.modules.produit.service.ProduitService;
import com.gros.modules.stock.service.StockService;
import com.gros.modules.vente.dto.CommandeResponse;
import com.gros.modules.vente.dto.CommandeVenteRequest;
import com.gros.modules.vente.entity.CommandeClient;
import com.gros.modules.vente.entity.LigneCommandeClient;
import com.gros.modules.vente.entity.StatutCommande;
import com.gros.modules.vente.repository.CommandeClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service @RequiredArgsConstructor
public class CommandeService extends ServiceDocument<CommandeClient> {
    private final CommandeClientRepository commandes;
    private final BonLivraisonRepository bons;
    private final ClientRepository clients;
    private final ProduitService produitService;
    private final StockService stock;
    private final RemiseService remises;
    private final SoldeClientService soldeClient;
    private final ParametreService parametres;
    private final NumerotationService numerotation;

    @Transactional
    public CommandeResponse creer(CommandeVenteRequest r) {
        Client client = clients.findById(r.clientId()).filter(Client::isActif)
                .orElseThrow(() -> new NotFoundException("Client introuvable"));
        CommandeClient c = new CommandeClient();
        c.setNumero(numerotation.prochainNumero("CMD"));
        c.setClientId(client.getId());
        c.setCreePar(SecurityUtils.idCourant());
        for (var l : r.lignes()) {
            Produit p = produitService.trouver(l.produitId());
            LigneCommandeClient lg = new LigneCommandeClient(p.getId(), l.quantite(), p.getPrixVente(), p.getTauxTva());
            lg.setRemisePourcent(remises.meilleureRemise(lg, client));
            c.getLignes().add(lg);
        }
        c.calculerTotaux();
        commandes.save(c);
        audit.tracer("CREATION", "COMMANDE_CLIENT", c.getId());
        return CommandeResponse.de(c);
    }

    @Transactional(readOnly = true)
    public CommandeResponse consulter(Long id) {
        return CommandeResponse.de(charger(id));
    }

    // ----- Template Method : valider() est hérité de ServiceDocument -----
    @Override protected CommandeClient charger(Long id) {
        return commandes.findById(id).orElseThrow(() -> new NotFoundException("Commande introuvable : " + id));
    }

    @Override protected void verifierRegles(CommandeClient c) {
        if (!c.getStatut().peutPasserA(StatutCommande.VALIDEE))
            throw new BusinessException("Cette commande ne peut plus être validée (statut " + c.getStatut() + ")");
        Client client = clients.findById(c.getClientId())
                .orElseThrow(() -> new NotFoundException("Client introuvable"));
        if (client.isBloque()) throw new BusinessException("Client bloqué par la direction");
        for (LigneCommandeClient l : c.getLignes()) {
            long dispo = stock.quantiteDisponible(l.getProduitId());
            if (dispo < l.getQuantite()) throw new BusinessException("Stock insuffisant : " + dispo + " disponibles");
        }
        if (!client.peutAcheterACredit(soldeClient.calculerSolde(client.getId()), c.getMontantTtc()))
            throw new BusinessException("Limite de crédit dépassée");
        boolean direction = SecurityUtils.utilisateurCourant().getRole() == Role.DIRECTEUR;
        BigDecimal plafond = parametres.decimal("PLAFOND_REMISE_POURCENT");
        if (!direction && c.getLignes().stream().anyMatch(l -> l.getRemisePourcent().compareTo(plafond) > 0))
            throw new BusinessException("Remise supérieure au plafond : validation de la direction requise");
    }

    @Override protected void appliquerEffets(CommandeClient c) { c.changerStatut(StatutCommande.VALIDEE); }
    @Override protected String nomEntite() { return "COMMANDE_CLIENT"; }
    @Override protected Long idDe(CommandeClient c) { return c.getId(); }

    @Transactional
    public BonLivraisonResponse preparer(Long id) {     // crée le bon de livraison
        CommandeClient c = charger(id);
        c.changerStatut(StatutCommande.EN_PREPARATION);
        BonLivraison bon = bons.save(new BonLivraison(numerotation.prochainNumero("BL"), id));
        audit.tracer("PREPARATION", "COMMANDE_CLIENT", id);
        return BonLivraisonResponse.de(bon);
    }

    @Transactional
    public CommandeResponse annuler(Long id) {
        CommandeClient c = charger(id);
        c.changerStatut(StatutCommande.ANNULEE);
        audit.tracer("ANNULATION", "COMMANDE_CLIENT", id);
        return CommandeResponse.de(c);
    }
}