package com.gros.modules.produit.controller;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

import com.gros.modules.produit.dto.HistoriquePrixResponse;
import com.gros.modules.produit.dto.ProduitRequest;
import com.gros.modules.produit.dto.ProduitResponse;
import com.gros.modules.produit.service.ProduitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private static final Set<String> VOIT_PRIX_ACHAT =
            Set.of("ROLE_ADMINISTRATEUR", "ROLE_DIRECTEUR", "ROLE_RESPONSABLE_ACHATS", "ROLE_COMPTABLE");

    private final ProduitService service;

    @GetMapping
    public Page<ProduitResponse> lister(@RequestParam(required = false) String q,
                                        Pageable pageable, Authentication auth) {
        boolean voit = auth.getAuthorities().stream()
                .anyMatch(a -> VOIT_PRIX_ACHAT.contains(a.getAuthority()));
        return service.lister(q, pageable).map(p -> voit ? p : p.sansPrixAchat());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','ADMINISTRATEUR')")
    public ProduitResponse creer(@Valid @RequestBody ProduitRequest r) {
        return service.creer(r);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','ADMINISTRATEUR')")
    public ProduitResponse modifier(@PathVariable Long id, @Valid @RequestBody ProduitRequest r) {
        return service.modifier(id, r);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','ADMINISTRATEUR')")
    public void desactiver(@PathVariable Long id) {
        service.desactiver(id);
    }

    @GetMapping("/{id}/historique-prix")
    public List<HistoriquePrixResponse> historique(@PathVariable Long id) {
        return service.historique(id);
    }
}