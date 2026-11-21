package com.gros.modules.client.controller;

import com.gros.modules.client.dto.BlocageAutomatiqueResponse;
import com.gros.modules.client.dto.ClientRequest;
import com.gros.modules.client.dto.ClientResponse;
import com.gros.modules.client.service.BlocageAutomatiqueService;
import com.gros.modules.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @RequestMapping("/api/clients") @RequiredArgsConstructor
public class ClientController {
    private final ClientService service;
    private final BlocageAutomatiqueService blocageAutomatique;

    @GetMapping
    public Page<ClientResponse> lister(@RequestParam(required = false) String q, Pageable pageable) {
        return service.lister(q, pageable);
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('COMMERCIAL','DIRECTEUR','ADMINISTRATEUR')")
    public ClientResponse creer(@Valid @RequestBody ClientRequest r) { return service.creer(r); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMMERCIAL','DIRECTEUR','ADMINISTRATEUR')")
    public ClientResponse modifier(@PathVariable Long id, @Valid @RequestBody ClientRequest r) {
        return service.modifier(id, r);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATEUR')")
    public void desactiver(@PathVariable Long id) { service.desactiver(id); }

    @PatchMapping("/{id}/bloquer") @PreAuthorize("hasRole('DIRECTEUR')")
    public void bloquer(@PathVariable Long id) { service.bloquer(id); }

    @PatchMapping("/{id}/debloquer") @PreAuthorize("hasRole('DIRECTEUR')")
    public void debloquer(@PathVariable Long id) { service.debloquer(id); }

    /** Même traitement que la tâche planifiée de 2 h du matin, déclenchable à la demande. */
    @PostMapping("/blocage-automatique")
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATEUR')")
    public BlocageAutomatiqueResponse bloquerClientsEnRetard() {
        List<Long> ids = blocageAutomatique.executer();
        return new BlocageAutomatiqueResponse(ids.size(), ids);
    }
}