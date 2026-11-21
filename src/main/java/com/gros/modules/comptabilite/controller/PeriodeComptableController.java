package com.gros.modules.comptabilite.controller;

import com.gros.modules.comptabilite.dto.PeriodeResponse;
import com.gros.modules.comptabilite.dto.ReglementTvaRequest;
import com.gros.modules.comptabilite.dto.TvaResponse;
import com.gros.modules.comptabilite.service.PeriodeComptableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;

@RestController @RequestMapping("/api/comptabilite") @RequiredArgsConstructor
public class PeriodeComptableController {
    private static final String LECTURE = "hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')";
    private final PeriodeComptableService service;

    @GetMapping("/tva")
    @PreAuthorize(LECTURE)
    public TvaResponse tva(@RequestParam int annee, @RequestParam int mois) {
        return service.calculerTva(annee, mois);
    }

    @GetMapping("/periodes")
    @PreAuthorize(LECTURE)
    public List<PeriodeResponse> periodes() { return service.lister(); }

    @PostMapping("/periodes/{annee}/{mois}/cloturer") @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATEUR')")
    public TvaResponse cloturer(@PathVariable int annee, @PathVariable int mois) {
        return service.cloturer(annee, mois);
    }

    @PostMapping("/periodes/{annee}/{mois}/regler-tva")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATEUR')")
    public PeriodeResponse reglerTva(@PathVariable int annee, @PathVariable int mois,
                                     @Valid @RequestBody ReglementTvaRequest r) {
        return service.reglerTva(annee, mois, r.modePaiement());
    }
}