package com.gros.modules.livraison.listener;

import com.gros.modules.livraison.entity.BonLivraison;
import com.gros.modules.livraison.event.LivraisonConfirmeeEvent;
import com.gros.modules.livraison.repository.BonLivraisonRepository;
import com.gros.modules.stock.entity.TypeMouvement;
import com.gros.modules.stock.service.StockService;
import com.gros.modules.vente.entity.CommandeClient;
import com.gros.modules.vente.entity.LigneCommandeClient;
import com.gros.modules.vente.entity.StatutCommande;
import com.gros.modules.vente.repository.CommandeClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class StockListener {
    private final BonLivraisonRepository bons;
    private final CommandeClientRepository commandes;
    private final StockService stock;

    @EventListener @Order(1)
    public void surLivraison(LivraisonConfirmeeEvent e) {
        BonLivraison bon = bons.findById(e.bonLivraisonId()).orElseThrow();
        CommandeClient c = commandes.findById(bon.getCommandeId()).orElseThrow();
        for (LigneCommandeClient l : c.getLignes())
            stock.enregistrerSortie(l.getProduitId(), l.getQuantite(), TypeMouvement.SORTIE_VENTE,
                    "BON_LIVRAISON", bon.getId());
        c.changerStatut(StatutCommande.LIVREE);
    }
}