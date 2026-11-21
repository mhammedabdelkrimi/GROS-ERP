package com.gros.modules.vente.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.NotFoundException;
import com.gros.common.numerotation.NumerotationService;
import com.gros.modules.client.entity.Client;
import com.gros.modules.client.repository.ClientRepository;
import com.gros.modules.produit.entity.Produit;
import com.gros.modules.produit.repository.ProduitRepository;
import com.gros.modules.vente.dto.DevisRequest;
import com.gros.modules.vente.dto.DevisResponse;
import com.gros.modules.vente.dto.LigneDevisResponse;
import com.gros.modules.vente.entity.Devis;
import com.gros.modules.vente.entity.LigneDevis;
import com.gros.modules.vente.entity.StatutDevis;
import com.gros.modules.vente.repository.DevisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class DevisService {
    private final DevisRepository devis;
    private final ClientRepository clients;
    private final ProduitRepository produits;
    private final CommandeService commandes;
    private final NumerotationService numerotation;
    private final AuditService audit;

    @Transactional
    public DevisResponse creer(DevisRequest r) {
        Client client = clients.findById(r.clientId()).filter(Client::isActif)
                .orElseThrow(() -> new NotFoundException("Client introuvable : " + r.clientId()));
        Devis d = new Devis();
        d.setNumero(numerotation.prochainNumero("DEV"));
        d.setClientId(client.getId());
        d.setDateValidite(LocalDate.now().plusDays(r.validiteJours() == null ? 30 : r.validiteJours()));
        for (var l : r.lignes()) {
            Produit p = produits.findById(l.produitId()).filter(Produit::isActif)
                    .orElseThrow(() -> new NotFoundException("Produit introuvable : " + l.produitId()));
            d.getLignes().add(new LigneDevis(p.getId(), l.quantite(), p.getPrixVente(), p.getTauxTva()));
        }
        d.calculerTotaux();
        devis.save(d);
        audit.tracer("CREATION", "DEVIS", d.getId());
        return versReponse(d);
    }

    @Transactional
    public DevisResponse envoyer(Long id) { return agir(id, "ENVOI", Devis::envoyer); }

    @Transactional
    public DevisResponse accepter(Long id) { return agir(id, "ACCEPTATION", Devis::accepter); }

    @Transactional
    public DevisResponse refuser(Long id) { return agir(id, "REFUS", Devis::refuser); }

    /** Crée la commande (BROUILLON) avec les prix et la TVA figés du devis. */
    @Transactional
    public DevisResponse convertir(Long id) {
        Devis d = verrou(id);
        d.verifierConvertible();                                  // avant de créer la commande
        Long commandeId = commandes.creerDepuisDevis(d.getClientId(), d.getLignes());
        d.marquerConverti(commandeId);
        audit.tracer("CONVERSION", "DEVIS", id);
        return versReponse(d);
    }

    @Transactional(readOnly = true)
    public DevisResponse consulter(Long id) {
        return versReponse(devis.findById(id).orElseThrow(() -> new NotFoundException("Devis introuvable : " + id)));
    }

    @Transactional(readOnly = true)
    public Page<DevisResponse> lister(StatutDevis statut, Pageable pageable) {
        var page = (statut == null) ? devis.findAll(pageable) : devis.findByStatut(statut, pageable);
        return page.map(this::versReponse);
    }

    private DevisResponse agir(Long id, String action, Consumer<Devis> operation) {
        Devis d = verrou(id);
        operation.accept(d);
        audit.tracer(action, "DEVIS", id);
        return versReponse(d);
    }

    private Devis verrou(Long id) {
        return devis.findByIdAvecVerrou(id).orElseThrow(() -> new NotFoundException("Devis introuvable : " + id));
    }

    private DevisResponse versReponse(Devis d) {
        Map<Long, String> noms = produits.findAllById(d.getLignes().stream().map(LigneDevis::getProduitId).toList())
                .stream().collect(Collectors.toMap(Produit::getId, Produit::getDesignation, (a, b) -> a));
        List<LigneDevisResponse> lignes = d.getLignes().stream()
                .map(l -> new LigneDevisResponse(l.getProduitId(), noms.getOrDefault(l.getProduitId(), "?"),
                        l.getQuantite(), l.getPrixUnitaire(), l.getTauxTva(), l.montantHT()))
                .toList();
        return new DevisResponse(d.getId(), d.getNumero(), d.getClientId(), d.getStatut(), d.getDateCreation(),
                d.getDateValidite(), d.estExpire(), d.getMontantHt(), d.getMontantTva(), d.getMontantTtc(),
                d.getCommandeId(), lignes);
    }
}