package com.gros.modules.livraison.controller;

import com.gros.modules.livraison.dto.BonLivraisonResponse;
import com.gros.modules.livraison.dto.RefusLivraisonRequest;
import com.gros.modules.livraison.entity.StatutBon;
import com.gros.modules.livraison.service.LivraisonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/livraisons") @RequiredArgsConstructor
public class LivraisonController {
    private final LivraisonService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIVREUR','MAGASINIER','DIRECTEUR','ADMINISTRATEUR')")
    public BonLivraisonResponse consulter(@PathVariable Long id) { return service.consulter(id); }

    @PutMapping("/{id}/signer")
    @PreAuthorize("hasRole('LIVREUR')")
    public BonLivraisonResponse signer(@PathVariable Long id) { return service.signer(id); }

    @PutMapping("/{id}/confirmer")
    @PreAuthorize("hasRole('LIVREUR')")
    public BonLivraisonResponse confirmer(@PathVariable Long id) { return service.confirmer(id); }

    @PutMapping("/{id}/refuser")
    @PreAuthorize("hasRole('LIVREUR')")
    public BonLivraisonResponse refuser(@PathVariable Long id, @Valid @RequestBody RefusLivraisonRequest r) {
        return service.refuser(id, r.motif());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LIVREUR','MAGASINIER','DIRECTEUR','ADMINISTRATEUR')")
    public Page<BonLivraisonResponse> lister(@RequestParam(required = false) StatutBon statut,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(defaultValue = "id,desc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction sens = (s.length > 1 && s[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(statut, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(sens, s[0])));
    }
}