package com.gros.modules.livraison.listener;

import com.gros.modules.livraison.event.LivraisonConfirmeeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class ComptabiliteListener {
    @EventListener @Order(3)
    public void surLivraison(LivraisonConfirmeeEvent e) {
        // Phase 2 (comptabilité) : créera ici l'écriture comptable de la facture.
    }
}