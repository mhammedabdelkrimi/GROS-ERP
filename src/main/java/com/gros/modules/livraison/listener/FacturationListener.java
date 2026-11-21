package com.gros.modules.livraison.listener;

import com.gros.common.numerotation.NumerotationService;
import com.gros.modules.client.entity.Client;
import com.gros.modules.client.repository.ClientRepository;
import com.gros.modules.facturation.builder.FactureBuilder;
import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.facturation.repository.FactureClientRepository;
import com.gros.modules.livraison.entity.BonLivraison;
import com.gros.modules.livraison.event.LivraisonConfirmeeEvent;
import com.gros.modules.livraison.repository.BonLivraisonRepository;
import com.gros.modules.produit.entity.Produit;
import com.gros.modules.produit.repository.ProduitRepository;
import com.gros.modules.vente.entity.CommandeClient;
import com.gros.modules.vente.entity.LigneCommandeClient;
import com.gros.modules.vente.entity.StatutCommande;
import com.gros.modules.vente.repository.CommandeClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component @RequiredArgsConstructor
public class FacturationListener {
    private final BonLivraisonRepository bons;
    private final CommandeClientRepository commandes;
    private final ClientRepository clients;
    private final ProduitRepository produits;
    private final FactureClientRepository factures;
    private final NumerotationService numerotation;

    @EventListener @Order(2)
    public void surLivraison(LivraisonConfirmeeEvent e) {
        BonLivraison bon = bons.findById(e.bonLivraisonId()).orElseThrow();
        CommandeClient c = commandes.findById(bon.getCommandeId()).orElseThrow();
        Client client = clients.findById(c.getClientId()).orElseThrow();
        Map<Long, Produit> parId = produits
                .findAllById(c.getLignes().stream().map(LigneCommandeClient::getProduitId).toList())
                .stream().collect(Collectors.toMap(Produit::getId, Function.identity()));
        Map<Long, String> noms = parId.values().stream()
                .collect(Collectors.toMap(Produit::getId, Produit::getDesignation));
        Map<Long, BigDecimal> couts = parId.values().stream()
                .collect(Collectors.toMap(Produit::getId, Produit::getPrixAchat));
        FactureClient f = new FactureBuilder()
                .pourBonLivraison(bon, c, noms, couts)
                .avecNumero(numerotation.prochainNumero("FAC"))      // sans trou : annulé avec la transaction
                .avecEcheance(LocalDate.now().plusDays(client.getDelaiPaiementJours()))
                .construire();
        factures.save(f);
        c.changerStatut(StatutCommande.FACTUREE);
    }
}