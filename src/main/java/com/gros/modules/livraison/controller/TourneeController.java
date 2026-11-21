package com.gros.modules.livraison.controller;

import com.gros.modules.livraison.dto.BonsTourneeRequest;
import com.gros.modules.livraison.dto.TourneeRequest;
import com.gros.modules.livraison.dto.TourneeResponse;
import com.gros.modules.livraison.service.TourneeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController @RequestMapping("/api/tournees") @RequiredArgsConstructor
public class TourneeController {
    private static final String GESTION = "hasAnyRole('SECRETAIRE_CAISSIER','DIRECTEUR','ADMINISTRATEUR')";
    private final TourneeService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize(GESTION)
    public TourneeResponse creer(@Valid @RequestBody TourneeRequest r) { return service.creer(r); }

    @PostMapping("/{id}/bons")
    @PreAuthorize(GESTION)
    public TourneeResponse ajouterBons(@PathVariable Long id, @Valid @RequestBody BonsTourneeRequest r) {
        return service.ajouterBons(id, r.bonIds());
    }

    @DeleteMapping("/{id}/bons/{bonId}")
    @PreAuthorize(GESTION)
    public TourneeResponse retirerBon(@PathVariable Long id, @PathVariable Long bonId) {
        return service.retirerBon(id, bonId);
    }

    @PutMapping("/{id}/demarrer")
    @PreAuthorize("hasAnyRole('LIVREUR','ADMINISTRATEUR')")
    public TourneeResponse demarrer(@PathVariable Long id) { return service.demarrer(id); }

    @PutMapping("/{id}/terminer")
    @PreAuthorize("hasAnyRole('LIVREUR','ADMINISTRATEUR')")
    public TourneeResponse terminer(@PathVariable Long id) { return service.terminer(id); }

    @GetMapping("/ma-tournee")
    @PreAuthorize("hasRole('LIVREUR')")
    public List<TourneeResponse> maTournee() { return service.maTournee(); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIVREUR','MAGASINIER','SECRETAIRE_CAISSIER','DIRECTEUR','ADMINISTRATEUR')")
    public TourneeResponse consulter(@PathVariable Long id) { return service.consulter(id); }

    @GetMapping
    @PreAuthorize("hasAnyRole('MAGASINIER','SECRETAIRE_CAISSIER','DIRECTEUR','ADMINISTRATEUR')")
    public Page<TourneeResponse> lister(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction sens = (s.length > 1 && s[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(date, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(sens, s[0])));
    }
}