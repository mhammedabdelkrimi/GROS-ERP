package com.gros.modules.paiement.controller;

import com.gros.modules.paiement.dto.PaiementRequest;
import com.gros.modules.paiement.dto.PaiementResponse;
import com.gros.modules.paiement.service.PaiementService;
import com.gros.modules.paiement.service.SoldeClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController @RequestMapping("/api/paiements") @RequiredArgsConstructor
public class PaiementController {
    private final PaiementService service;
    private final SoldeClientService soldeClient;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SECRETAIRE_CAISSIER','ADMINISTRATEUR')")
    public PaiementResponse enregistrer(@Valid @RequestBody PaiementRequest r) { return service.enregistrer(r); }

    @GetMapping("/solde/{clientId}")
    @PreAuthorize("hasAnyRole('SECRETAIRE_CAISSIER','COMPTABLE','COMMERCIAL','DIRECTEUR','ADMINISTRATEUR')")
    public BigDecimal solde(@PathVariable Long clientId) { return soldeClient.calculerSolde(clientId); }
}