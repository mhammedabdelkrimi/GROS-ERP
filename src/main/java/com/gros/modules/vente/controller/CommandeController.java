package com.gros.modules.vente.controller;

import com.gros.modules.livraison.dto.BonLivraisonResponse;
import com.gros.modules.vente.dto.CommandeResponse;
import com.gros.modules.vente.dto.CommandeVenteRequest;
import com.gros.modules.vente.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/commandes") @RequiredArgsConstructor
public class CommandeController {
    private final CommandeService service;

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