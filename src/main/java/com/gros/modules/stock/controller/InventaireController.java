package com.gros.modules.stock.controller;

import com.gros.modules.stock.dto.ComptageRequest;
import com.gros.modules.stock.dto.InventaireResponse;
import com.gros.modules.stock.dto.InventaireResume;
import com.gros.modules.stock.dto.OuvertureInventaireRequest;
import com.gros.modules.stock.service.InventaireService;
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

@RestController @RequestMapping("/api/inventaires") @RequiredArgsConstructor
public class InventaireController {
    private final InventaireService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MAGASINIER','ADMINISTRATEUR')")
    public InventaireResponse ouvrir(@RequestBody(required = false) OuvertureInventaireRequest r) {
        return service.ouvrir(r);
    }

    @PutMapping("/{id}/comptage")
    @PreAuthorize("hasAnyRole('MAGASINIER','ADMINISTRATEUR')")
    public InventaireResponse compter(@PathVariable Long id, @Valid @RequestBody ComptageRequest r) {
        return service.compter(id, r);
    }

    @PutMapping("/{id}/cloturer")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATEUR')")
    public InventaireResponse cloturer(@PathVariable Long id) { return service.cloturer(id); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MAGASINIER','DIRECTEUR','COMPTABLE','ADMINISTRATEUR')")
    public InventaireResponse consulter(@PathVariable Long id) { return service.consulter(id); }

    @GetMapping
    @PreAuthorize("hasAnyRole('MAGASINIER','DIRECTEUR','COMPTABLE','ADMINISTRATEUR')")
    public Page<InventaireResume> lister(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "id,desc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction sens = (s.length > 1 && s[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(sens, s[0])));
    }
}