package com.gros.modules.livraison.service;

import com.gros.modules.livraison.entity.StatutBon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.gros.common.audit.AuditService;
import com.gros.common.exception.NotFoundException;
import com.gros.modules.livraison.dto.BonLivraisonResponse;
import com.gros.modules.livraison.entity.BonLivraison;
import com.gros.modules.livraison.event.LivraisonConfirmeeEvent;
import com.gros.modules.livraison.repository.BonLivraisonRepository;
import com.gros.modules.vente.entity.CommandeClient;
import com.gros.modules.vente.entity.StatutCommande;
import com.gros.modules.vente.repository.CommandeClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class LivraisonService {
    private final BonLivraisonRepository bons;
    private final CommandeClientRepository commandes;
    private final ApplicationEventPublisher publisher;
    private final AuditService audit;

    private BonLivraison trouver(Long id) {
        return bons.findById(id).orElseThrow(() -> new NotFoundException("Bon introuvable"));
    }

    @Transactional(readOnly = true)
    public BonLivraisonResponse consulter(Long id) {
        return BonLivraisonResponse.de(trouver(id));
    }

    @Transactional
    public BonLivraisonResponse signer(Long bonId) {
        BonLivraison bon = trouver(bonId);
        bon.faireSigner();
        audit.tracer("SIGNATURE", "BON_LIVRAISON", bonId);
        return BonLivraisonResponse.de(bon);
    }

    @Transactional   // UNE seule transaction : COMMIT si tout réussit, ROLLBACK sinon
    public BonLivraisonResponse confirmer(Long bonId) {
        BonLivraison bon = trouver(bonId);
        bon.confirmer();                                               // refuse si non signé
        publisher.publishEvent(new LivraisonConfirmeeEvent(bonId));    // stock + facture
        audit.tracer("CONFIRMATION", "BON_LIVRAISON", bonId);
        return BonLivraisonResponse.de(bon);
    }

    @Transactional
    public BonLivraisonResponse refuser(Long bonId, String motif) {
        BonLivraison bon = trouver(bonId);
        bon.refuser(motif);
        CommandeClient c = commandes.findById(bon.getCommandeId())
                .orElseThrow(() -> new NotFoundException("Commande introuvable"));
        c.changerStatut(StatutCommande.ANNULEE);    // aucun stock n'est sorti : rien à annuler
        audit.tracer("REFUS", "BON_LIVRAISON", bonId);
        return BonLivraisonResponse.de(bon);
    }

    @Transactional(readOnly = true)
    public Page<BonLivraisonResponse> lister(StatutBon statut, Pageable pageable) {
        var page = (statut == null) ? bons.findAll(pageable) : bons.findByStatut(statut, pageable);
        return page.map(BonLivraisonResponse::de);
    }
}