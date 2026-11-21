package com.gros.modules.achat.controller;

import com.gros.modules.achat.dto.AvoirFournisseurRequest;
import com.gros.modules.achat.dto.AvoirFournisseurResponse;
import com.gros.modules.achat.dto.FactureFournisseurResponse;
import com.gros.modules.achat.dto.RemboursementFournisseurRequest;
import com.gros.modules.achat.service.AvoirFournisseurService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class AvoirFournisseurController {
    private final AvoirFournisseurService service;

    @PostMapping("/avoirs-fournisseurs") @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('COMPTABLE','RESPONSABLE_ACHATS','ADMINISTRATEUR')")
    public AvoirFournisseurResponse creer(@Valid @RequestBody AvoirFournisseurRequest r) {
        return service.creer(r);
    }

    @GetMapping("/avoirs-fournisseurs")
    @PreAuthorize("hasAnyRole('COMPTABLE','RESPONSABLE_ACHATS','DIRECTEUR','ADMINISTRATEUR')")
    public Page<AvoirFournisseurResponse> lister(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(defaultValue = "id,desc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction sens = (s.length > 1 && s[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(sens, s[0])));
    }

    @PostMapping("/factures-fournisseurs/{id}/remboursements")
    @PreAuthorize("hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")
    public FactureFournisseurResponse rembourser(@PathVariable Long id,
                                                 @Valid @RequestBody RemboursementFournisseurRequest r) {
        return service.rembourser(id, r);
    }
}