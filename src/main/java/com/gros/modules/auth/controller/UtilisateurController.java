package com.gros.modules.auth.controller;

import com.gros.modules.auth.dto.UtilisateurRequest;
import com.gros.modules.auth.dto.UtilisateurResponse;
import com.gros.modules.auth.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/utilisateurs") @RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRATEUR')")
public class UtilisateurController {
    private final UtilisateurService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurResponse creer(@Valid @RequestBody UtilisateurRequest r) { return service.creer(r); }

    @PatchMapping("/{id}/desactiver")
    public void desactiver(@PathVariable Long id) { service.desactiver(id); }
}