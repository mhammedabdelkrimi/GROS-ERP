package com.gros.modules.livraison.listener;

import com.gros.common.exception.NotFoundException;
import com.gros.modules.comptabilite.service.ComptabiliteService;
import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.facturation.repository.FactureClientRepository;
import com.gros.modules.livraison.event.LivraisonConfirmeeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor
public class ComptabiliteListener {
    private final FactureClientRepository factures;
    private final ComptabiliteService comptabilite;

    @EventListener @Order(3)
    public void surLivraison(LivraisonConfirmeeEvent e) {
        FactureClient f = factures.findByBonLivraisonId(e.bonLivraisonId())
                .orElseThrow(() -> new NotFoundException("Facture introuvable pour le bon " + e.bonLivraisonId()));
        comptabilite.comptabiliserFacture(f);   // même transaction : si elle échoue, tout est annulé
    }
}