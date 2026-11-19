package com.gros.modules.facturation.controller;

import java.util.List;
import com.gros.modules.facturation.dto.FactureResponse;
import com.gros.modules.facturation.service.FactureService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/factures") @RequiredArgsConstructor
public class FactureController {
    private final FactureService service;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPTABLE','SECRETAIRE_CAISSIER','COMMERCIAL','DIRECTEUR','ADMINISTRATEUR')")
    public FactureResponse consulter(@PathVariable Long id) { return service.consulter(id); }
    @GetMapping("/impayees")
    @PreAuthorize("hasAnyRole('COMPTABLE','SECRETAIRE_CAISSIER','DIRECTEUR','ADMINISTRATEUR')")
    public List<FactureResponse> impayees() { return service.impayees(); }
}