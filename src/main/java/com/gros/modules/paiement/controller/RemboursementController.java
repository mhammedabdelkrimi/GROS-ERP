package com.gros.modules.paiement.controller;

import com.gros.modules.paiement.dto.CreditClientResponse;
import com.gros.modules.paiement.dto.RemboursementRequest;
import com.gros.modules.paiement.dto.RemboursementResponse;
import com.gros.modules.paiement.service.RemboursementService;
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

@RestController @RequestMapping("/api/remboursements") @RequiredArgsConstructor
public class RemboursementController {
    private final RemboursementService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('DIRECTEUR','SECRETAIRE_CAISSIER','ADMINISTRATEUR')")
    public RemboursementResponse enregistrer(@Valid @RequestBody RemboursementRequest r) {
        return service.enregistrer(r);
    }

    @GetMapping("/credit/{clientId}")
    @PreAuthorize("hasAnyRole('DIRECTEUR','SECRETAIRE_CAISSIER','COMMERCIAL','COMPTABLE','ADMINISTRATEUR')")
    public CreditClientResponse credit(@PathVariable Long clientId) { return service.credit(clientId); }
}