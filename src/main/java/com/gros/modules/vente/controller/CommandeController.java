package com.gros.modules.vente.controller;

import com.gros.modules.livraison.dto.BonLivraisonResponse;
import com.gros.modules.vente.dto.CommandeResponse;
import com.gros.modules.vente.dto.CommandeResume;
import com.gros.modules.vente.dto.CommandeVenteRequest;
import com.gros.modules.vente.entity.StatutCommande;
import com.gros.modules.vente.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/commandes") @RequiredArgsConstructor
public class CommandeController {
    private final CommandeService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('COMMERCIAL','SECRETAIRE_CAISSIER','DIRECTEUR','MAGASINIER','ADMINISTRATEUR')")
    public Page<CommandeResume> lister(@RequestParam(required = false) StatutCommande statut,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "id,desc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction sens = (s.length > 1 && s[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(statut, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(sens, s[0])));
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('COMMERCIAL','SECRETAIRE_CAISSIER')")
    public CommandeResponse creer(@Valid @RequestBody CommandeVenteRequest r) { return service.creer(r); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMMERCIAL','SECRETAIRE_CAISSIER','DIRECTEUR','MAGASINIER','ADMINISTRATEUR')")
    public CommandeResponse consulter(@PathVariable Long id) { return service.consulter(id); }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasAnyRole('COMMERCIAL','DIRECTEUR')")
    public void valider(@PathVariable Long id) { service.valider(id); }

    @PutMapping("/{id}/preparer")
    @PreAuthorize("hasRole('MAGASINIER')")
    public BonLivraisonResponse preparer(@PathVariable Long id) { return service.preparer(id); }

    @PutMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('COMMERCIAL','DIRECTEUR')")
    public CommandeResponse annuler(@PathVariable Long id) { return service.annuler(id); }
}