package com.gros.modules.pilotage.controller;

import com.gros.modules.pilotage.dto.CaAnnuelResponse;
import com.gros.modules.pilotage.dto.ClientImpayeResponse;
import com.gros.modules.pilotage.dto.ClientPerformanceResponse;
import com.gros.modules.pilotage.dto.ImpayesResponse;
import com.gros.modules.pilotage.dto.ProduitInactifResponse;
import com.gros.modules.pilotage.dto.ProduitPerformanceResponse;
import com.gros.modules.pilotage.dto.StockPilotageResponse;
import com.gros.modules.pilotage.dto.SyntheseResponse;
import com.gros.modules.pilotage.service.PilotageService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** Données sensibles (marges, valeur du stock) : réservé à la direction, à la comptabilité et à l'administration (BNF-08). */
@RestController @RequestMapping("/api/pilotage") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('DIRECTEUR','COMPTABLE','ADMINISTRATEUR')")
public class PilotageController {
    private final PilotageService service;

    @GetMapping("/synthese")
    public SyntheseResponse synthese() { return service.synthese(); }

    @GetMapping("/ca")
    public CaAnnuelResponse chiffreAffaires(@RequestParam(defaultValue = "#{T(java.time.Year).now().getValue()}") int annee) {
        return service.chiffreAffaires(annee);
    }

    /** tri : quantite, ca ou marge. Par défaut : du 1er janvier à aujourd'hui. */
    @GetMapping("/produits")
    public List<ProduitPerformanceResponse> produits(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate du,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate au,
            @RequestParam(defaultValue = "ca") String tri,
            @RequestParam(defaultValue = "10") int limite) {
        return service.produits(du, au, tri, limite);
    }

    @GetMapping("/produits/inactifs")
    public List<ProduitInactifResponse> produitsInactifs(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate du,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate au,
            @RequestParam(defaultValue = "50") int limite) {
        return service.produitsInactifs(du, au, limite);
    }

    /** tri : ca ou marge. */
    @GetMapping("/clients")
    public List<ClientPerformanceResponse> clients(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate du,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate au,
            @RequestParam(defaultValue = "ca") String tri,
            @RequestParam(defaultValue = "10") int limite) {
        return service.clients(du, au, tri, limite);
    }

    @GetMapping("/clients/impayes")
    public List<ClientImpayeResponse> clientsImpayes(@RequestParam(defaultValue = "10") int limite) {
        return service.clientsImpayes(limite);
    }

    @GetMapping("/impayes")
    public ImpayesResponse impayes() { return service.impayes(); }

    @GetMapping("/stock")
    public StockPilotageResponse stock() { return service.stock(); }
}