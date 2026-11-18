package com.gros.modules.fournisseur.controller;

import com.gros.modules.fournisseur.dto.FournisseurRequest;
import com.gros.modules.fournisseur.dto.FournisseurResponse;
import com.gros.modules.fournisseur.service.FournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@RestController @RequestMapping("/api/fournisseurs") @RequiredArgsConstructor
public class FournisseurController {
    private final FournisseurService service;

    @GetMapping
    public Page<FournisseurResponse> lister(@RequestParam(required = false) String q, Pageable pageable) {
        return service.lister(q, pageable);
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','ADMINISTRATEUR')")
    public FournisseurResponse creer(@Valid @RequestBody FournisseurRequest r) { return service.creer(r); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','ADMINISTRATEUR')")
    public FournisseurResponse modifier(@PathVariable Long id, @Valid @RequestBody FournisseurRequest r) {
        return service.modifier(id, r);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','ADMINISTRATEUR')")
    public void desactiver(@PathVariable Long id) { service.desactiver(id); }
}