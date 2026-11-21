package com.gros.modules.comptabilite.controller;

import com.gros.modules.comptabilite.dto.BalanceResponse;
import com.gros.modules.comptabilite.dto.CompteResponse;
import com.gros.modules.comptabilite.dto.EcritureResponse;
import com.gros.modules.comptabilite.dto.ExtourneRequest;
import com.gros.modules.comptabilite.service.ComptabiliteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

@RestController @RequestMapping("/api/comptabilite") @RequiredArgsConstructor
public class ComptabiliteController {
    private static final String LECTURE = "hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')";
    private final ComptabiliteService service;

    @GetMapping("/comptes")
    @PreAuthorize(LECTURE)
    public List<CompteResponse> comptes() { return service.comptes(); }

    @GetMapping("/ecritures")
    @PreAuthorize(LECTURE)
    public Page<EcritureResponse> journal(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate du,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate au,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction sens = (s.length > 1 && s[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.journal(du, au,
                PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(sens, s[0])));
    }

    @GetMapping("/ecritures/{id}")
    @PreAuthorize(LECTURE)
    public EcritureResponse consulter(@PathVariable Long id) { return service.consulter(id); }

    @GetMapping("/balance")
    @PreAuthorize(LECTURE)
    public BalanceResponse balance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate du,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate au) {
        return service.balance(du, au);
    }

    @PostMapping("/ecritures/{id}/extourner") @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('DIRECTEUR','ADMINISTRATEUR')")
    public EcritureResponse extourner(@PathVariable Long id, @Valid @RequestBody ExtourneRequest r) {
        return service.extourner(id, r.motif());
    }
}