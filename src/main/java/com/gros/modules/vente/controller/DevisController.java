package com.gros.modules.vente.controller;

import com.gros.modules.vente.dto.DevisRequest;
import com.gros.modules.vente.dto.DevisResponse;
import com.gros.modules.vente.entity.StatutDevis;
import com.gros.modules.vente.service.DevisService;
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

@RestController @RequestMapping("/api/devis") @RequiredArgsConstructor
public class DevisController {
    private static final String ECRITURE = "hasAnyRole('COMMERCIAL','SECRETAIRE_CAISSIER','ADMINISTRATEUR')";
    private static final String LECTURE = "hasAnyRole('COMMERCIAL','SECRETAIRE_CAISSIER','DIRECTEUR','ADMINISTRATEUR')";
    private final DevisService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(ECRITURE)
    public DevisResponse creer(@Valid @RequestBody DevisRequest r) { return service.creer(r); }

    @GetMapping("/{id}")
    @PreAuthorize(LECTURE)
    public DevisResponse consulter(@PathVariable Long id) { return service.consulter(id); }

    @GetMapping
    @PreAuthorize(LECTURE)
    public Page<DevisResponse> lister(@RequestParam(required = false) StatutDevis statut,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "id,desc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction sens = (s.length > 1 && s[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(statut, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(sens, s[0])));
    }

    @PutMapping("/{id}/envoyer")
    @PreAuthorize(ECRITURE)
    public DevisResponse envoyer(@PathVariable Long id) { return service.envoyer(id); }

    @PutMapping("/{id}/accepter")
    @PreAuthorize(ECRITURE)
    public DevisResponse accepter(@PathVariable Long id) { return service.accepter(id); }

    @PutMapping("/{id}/refuser")
    @PreAuthorize(ECRITURE)
    public DevisResponse refuser(@PathVariable Long id) { return service.refuser(id); }

    @PutMapping("/{id}/convertir")
    @PreAuthorize("hasAnyRole('COMMERCIAL','SECRETAIRE_CAISSIER')")
    public DevisResponse convertir(@PathVariable Long id) { return service.convertir(id); }
}