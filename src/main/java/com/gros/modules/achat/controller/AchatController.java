package com.gros.modules.achat.controller;

import com.gros.modules.achat.dto.CommandeAchatRequest;
import com.gros.modules.achat.dto.CommandeAchatResponse;
import com.gros.modules.achat.dto.ReceptionRequest;
import com.gros.modules.achat.service.AchatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/achats") @RequiredArgsConstructor
public class AchatController {
    private final AchatService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('RESPONSABLE_ACHATS')")
    public CommandeAchatResponse creer(@Valid @RequestBody CommandeAchatRequest r) { return service.creer(r); }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','DIRECTEUR','MAGASINIER','ADMINISTRATEUR')")
    public CommandeAchatResponse consulter(@PathVariable Long id) { return service.consulter(id); }

    @PutMapping("/{id}/valider")
    @PreAuthorize("hasAnyRole('RESPONSABLE_ACHATS','DIRECTEUR')")
    public void valider(@PathVariable Long id) { service.valider(id); }

    @PostMapping("/{id}/receptions")
    @PreAuthorize("hasRole('MAGASINIER')")
    public CommandeAchatResponse receptionner(@PathVariable Long id, @Valid @RequestBody ReceptionRequest r) {
        return service.receptionner(id, r);
    }
}