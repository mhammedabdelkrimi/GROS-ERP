package com.gros.modules.facturation.controller;

import com.gros.modules.facturation.dto.FactureResponse;
import com.gros.modules.facturation.entity.StatutFacture;
import com.gros.modules.facturation.service.FactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @RequestMapping("/api/factures") @RequiredArgsConstructor
public class FactureController {
    private final FactureService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPTABLE','SECRETAIRE_CAISSIER','COMMERCIAL','DIRECTEUR','ADMINISTRATEUR')")
    public FactureResponse consulter(@PathVariable Long id) { return service.consulter(id); }

    @GetMapping("/impayees")
    @PreAuthorize("hasAnyRole('COMPTABLE','SECRETAIRE_CAISSIER','DIRECTEUR','ADMINISTRATEUR')")
    public List<FactureResponse> impayees() { return service.impayees(); }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPTABLE','SECRETAIRE_CAISSIER','COMMERCIAL','DIRECTEUR','ADMINISTRATEUR')")
    public Page<FactureResponse> lister(@RequestParam(required = false) StatutFacture statut,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "id,desc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction sens = (s.length > 1 && s[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(statut, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(sens, s[0])));
    }
}