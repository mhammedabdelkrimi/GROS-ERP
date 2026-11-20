package com.gros.modules.achat.service;

import com.gros.modules.achat.dto.AchatResume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.gros.common.SecurityUtils;
import com.gros.common.document.ServiceDocument;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.common.numerotation.NumerotationService;
import com.gros.common.parametre.ParametreService;
import com.gros.modules.achat.dto.CommandeAchatRequest;
import com.gros.modules.achat.dto.CommandeAchatResponse;
import com.gros.modules.achat.dto.ReceptionRequest;
import com.gros.modules.achat.entity.CommandeFournisseur;
import com.gros.modules.achat.entity.Reception;
import com.gros.modules.achat.entity.StatutCommandeAchat;
import com.gros.modules.achat.repository.CommandeFournisseurRepository;
import com.gros.modules.achat.repository.ReceptionRepository;
import com.gros.modules.auth.entity.Role;
import com.gros.modules.fournisseur.entity.Fournisseur;
import com.gros.modules.fournisseur.repository.FournisseurRepository;
import com.gros.modules.produit.repository.ProduitRepository;
import com.gros.modules.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class AchatService extends ServiceDocument<CommandeFournisseur> {
    private final CommandeFournisseurRepository commandes;
    private final ReceptionRepository receptions;
    private final FournisseurRepository fournisseurs;
    private final ProduitRepository produits;
    private final StockService stock;
    private final NumerotationService numerotation;
    private final ParametreService parametres;

    @Transactional
    public CommandeAchatResponse creer(CommandeAchatRequest r) {
        fournisseurs.findById(r.fournisseurId()).filter(Fournisseur::isActif)
                .orElseThrow(() -> new NotFoundException("Fournisseur introuvable : " + r.fournisseurId()));
        CommandeFournisseur c = new CommandeFournisseur();
        c.setNumero(numerotation.prochainNumero("CF"));
        c.setFournisseurId(r.fournisseurId());
        c.setCreePar(SecurityUtils.idCourant());
        for (var l : r.lignes()) {
            if (!produits.existsById(l.produitId()))
                throw new NotFoundException("Produit introuvable : " + l.produitId());
            c.ajouterLigne(l.produitId(), l.quantite(), l.prixUnitaire());
        }
        c.calculerTotal();
        commandes.save(c);
        audit.tracer("CREATION", "COMMANDE_FOURNISSEUR", c.getId());
        return CommandeAchatResponse.de(c);
    }

    @Transactional(readOnly = true)
    public CommandeAchatResponse consulter(Long id) {
        return CommandeAchatResponse.de(charger(id));
    }

    // ----- Template Method : valider() est hérité de ServiceDocument -----
    @Override protected CommandeFournisseur charger(Long id) {
        return commandes.findById(id).orElseThrow(() -> new NotFoundException("Commande introuvable : " + id));
    }

    @Override protected void verifierRegles(CommandeFournisseur c) {
        if (c.getStatut() != StatutCommandeAchat.BROUILLON)
            throw new BusinessException("Seule une commande en brouillon peut être validée");
        boolean direction = SecurityUtils.utilisateurCourant().getRole() == Role.DIRECTEUR;
        if (!direction && c.getMontantTotal().compareTo(parametres.decimal("SEUIL_VALIDATION_ACHAT")) > 0)
            throw new BusinessException("Montant supérieur au seuil : validation de la direction requise");
    }

    @Override protected void appliquerEffets(CommandeFournisseur c) { c.setStatut(StatutCommandeAchat.VALIDEE); }
    @Override protected String nomEntite() { return "COMMANDE_FOURNISSEUR"; }
    @Override protected Long idDe(CommandeFournisseur c) { return c.getId(); }

    // ----- Réception : le stock augmente -----
    @Transactional
    public CommandeAchatResponse receptionner(Long commandeId, ReceptionRequest r) {
        CommandeFournisseur c = charger(commandeId);
        if (c.getStatut() != StatutCommandeAchat.VALIDEE && c.getStatut() != StatutCommandeAchat.PARTIELLEMENT_RECUE)
            throw new BusinessException("Cette commande ne peut pas être réceptionnée");
        Reception rec = receptions.save(new Reception(numerotation.prochainNumero("REC"), commandeId));
        for (var l : r.lignes()) {
            var ligneCmd = c.getLignes().stream()
                    .filter(x -> x.getProduitId().equals(l.produitId())).findFirst()
                    .orElseThrow(() -> new BusinessException("Produit " + l.produitId() + " absent de la commande"));
            rec.ajouterLigne(l.produitId(), l.quantiteRecue(), l.numeroLot(), l.datePeremption());
            stock.enregistrerEntree(l.produitId(), l.quantiteRecue(), l.numeroLot(), l.datePeremption(),
                    "RECEPTION", rec.getId());
            ligneCmd.setQuantiteRecue(ligneCmd.getQuantiteRecue() + l.quantiteRecue());
        }
        c.setStatut(c.estCompletementRecue() ? StatutCommandeAchat.RECUE : StatutCommandeAchat.PARTIELLEMENT_RECUE);
        audit.tracer("RECEPTION", "COMMANDE_FOURNISSEUR", commandeId);
        return CommandeAchatResponse.de(c);
    }
    @Transactional(readOnly = true)
    public Page<AchatResume> lister(StatutCommandeAchat statut, Pageable pageable) {
        Page<CommandeFournisseur> page = (statut == null)
                ? commandes.findAll(pageable)
                : commandes.findByStatut(statut, pageable);
        return page.map(AchatResume::de);
    }
}