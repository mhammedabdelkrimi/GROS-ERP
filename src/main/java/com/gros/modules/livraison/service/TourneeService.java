package com.gros.modules.livraison.service;

import com.gros.common.SecurityUtils;
import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.common.numerotation.NumerotationService;
import com.gros.modules.auth.entity.Role;
import com.gros.modules.auth.entity.Utilisateur;
import com.gros.modules.auth.repository.UtilisateurRepository;
import com.gros.modules.client.entity.Client;
import com.gros.modules.client.repository.ClientRepository;
import com.gros.modules.livraison.dto.BonTourneeResponse;
import com.gros.modules.livraison.dto.TourneeRequest;
import com.gros.modules.livraison.dto.TourneeResponse;
import com.gros.modules.livraison.entity.BonLivraison;
import com.gros.modules.livraison.entity.StatutBon;
import com.gros.modules.livraison.entity.Tournee;
import com.gros.modules.livraison.repository.BonLivraisonRepository;
import com.gros.modules.livraison.repository.TourneeRepository;
import com.gros.modules.vente.entity.CommandeClient;
import com.gros.modules.vente.repository.CommandeClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class TourneeService {
    private final TourneeRepository tournees;
    private final BonLivraisonRepository bons;
    private final CommandeClientRepository commandes;
    private final ClientRepository clients;
    private final UtilisateurRepository utilisateurs;
    private final NumerotationService numerotation;
    private final AuditService audit;

    @Transactional
    public TourneeResponse creer(TourneeRequest r) {
        if (r.dateTournee().isBefore(LocalDate.now()))
            throw new BusinessException("La date de la tournée ne peut pas être dans le passé");
        Utilisateur livreur = utilisateurs.findById(r.livreurId())
                .filter(Utilisateur::isActif).filter(u -> u.getRole() == Role.LIVREUR)
                .orElseThrow(() -> new BusinessException("Livreur introuvable ou rôle incorrect : " + r.livreurId()));
        Tournee t = new Tournee();
        t.setNumero(numerotation.prochainNumero("TOU"));
        t.setDateTournee(r.dateTournee());
        t.setLivreurId(livreur.getId());
        if (r.bonIds() != null)
            r.bonIds().stream().distinct().forEach(id -> { verifierBonAffectable(id); t.ajouterBon(id); });
        tournees.save(t);
        audit.tracer("CREATION", "TOURNEE", t.getId());
        return versReponse(t);
    }

    @Transactional
    public TourneeResponse ajouterBons(Long id, List<Long> bonIds) {
        Tournee t = verrou(id);
        bonIds.stream().distinct().forEach(b -> { verifierBonAffectable(b); t.ajouterBon(b); });
        audit.tracer("MODIFICATION", "TOURNEE", id);
        return versReponse(t);
    }

    @Transactional
    public TourneeResponse retirerBon(Long id, Long bonId) {
        Tournee t = verrou(id);
        t.retirerBon(bonId);
        audit.tracer("MODIFICATION", "TOURNEE", id);
        return versReponse(t);
    }

    @Transactional
    public TourneeResponse demarrer(Long id) {
        Tournee t = verrou(id);
        verifierAcces(t);
        t.demarrer();
        audit.tracer("DEMARRAGE", "TOURNEE", id);
        return versReponse(t);
    }

    /** Une tournée ne se termine que lorsque chaque bon est confirmé ou refusé. */
    @Transactional
    public TourneeResponse terminer(Long id) {
        Tournee t = verrou(id);
        verifierAcces(t);
        String restants = bons.findAllById(t.getBonIds()).stream()
                .filter(b -> b.getStatut() == StatutBon.CREE)
                .map(BonLivraison::getNumero).collect(Collectors.joining(", "));
        if (!restants.isEmpty())
            throw new BusinessException("Bons encore à livrer : " + restants);
        t.terminer();
        audit.tracer("CLOTURE", "TOURNEE", id);
        return versReponse(t);
    }

    @Transactional(readOnly = true)
    public TourneeResponse consulter(Long id) {
        Tournee t = tournees.findById(id).orElseThrow(() -> new NotFoundException("Tournée introuvable : " + id));
        verifierAcces(t);
        return versReponse(t);
    }

    /** Les tournées du jour du livreur connecté. */
    @Transactional(readOnly = true)
    public List<TourneeResponse> maTournee() {
        return tournees.findByLivreurIdAndDateTourneeOrderByIdAsc(SecurityUtils.idCourant(), LocalDate.now())
                .stream().map(this::versReponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<TourneeResponse> lister(LocalDate date, Pageable pageable) {
        var page = (date == null) ? tournees.findAll(pageable) : tournees.findByDateTournee(date, pageable);
        return page.map(this::versReponse);
    }

    private void verifierBonAffectable(Long bonId) {
        BonLivraison bon = bons.findById(bonId)
                .orElseThrow(() -> new NotFoundException("Bon de livraison introuvable : " + bonId));
        if (bon.getStatut() != StatutBon.CREE)
            throw new BusinessException("Le bon " + bon.getNumero() + " n'est plus à livrer (statut " + bon.getStatut() + ")");
        if (tournees.compterBon(bonId) > 0)
            throw new BusinessException("Le bon " + bon.getNumero() + " est déjà affecté à une tournée");
    }

    /** Un livreur ne manipule que ses propres tournées. */
    private void verifierAcces(Tournee t) {
        Utilisateur u = SecurityUtils.utilisateurCourant();
        if (u != null && u.getRole() == Role.LIVREUR && !u.getId().equals(t.getLivreurId()))
            throw new AccessDeniedException("Cette tournée est affectée à un autre livreur");
    }

    private Tournee verrou(Long id) {
        return tournees.findByIdAvecVerrou(id).orElseThrow(() -> new NotFoundException("Tournée introuvable : " + id));
    }

    private TourneeResponse versReponse(Tournee t) {
        List<BonLivraison> liste = bons.findAllById(t.getBonIds());
        Map<Long, CommandeClient> parCommande = commandes
                .findAllById(liste.stream().map(BonLivraison::getCommandeId).toList()).stream()
                .collect(Collectors.toMap(CommandeClient::getId, Function.identity()));
        Map<Long, Client> parClient = clients
                .findAllById(parCommande.values().stream().map(CommandeClient::getClientId).distinct().toList()).stream()
                .collect(Collectors.toMap(Client::getId, Function.identity()));
        List<BonTourneeResponse> details = liste.stream()
                .sorted(Comparator.comparing(BonLivraison::getId))
                .map(b -> {
                    CommandeClient c = parCommande.get(b.getCommandeId());
                    Client cl = c == null ? null : parClient.get(c.getClientId());
                    return new BonTourneeResponse(b.getId(), b.getNumero(), b.getStatut(), b.isSigneParClient(),
                            b.getCommandeId(), cl == null ? null : cl.getId(),
                            cl == null ? "?" : cl.getRaisonSociale(),
                            cl == null ? null : cl.getAdresse(), cl == null ? null : cl.getTelephone());
                }).toList();
        String nom = utilisateurs.findById(t.getLivreurId()).map(Utilisateur::getNom).orElse("?");
        return new TourneeResponse(t.getId(), t.getNumero(), t.getDateTournee(), t.getLivreurId(), nom,
                t.getStatut(), details);
    }
}