package com.gros.modules.facturation.controller;

import com.gros.modules.facturation.dto.AvoirRequest;
import com.gros.modules.facturation.dto.AvoirResponse;
import com.gros.modules.facturation.service.AvoirService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/avoirs") @RequiredArgsConstructor
public class AvoirController {
    private final AvoirService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('DIRECTEUR','SECRETAIRE_CAISSIER','ADMINISTRATEUR')")
    public AvoirResponse creer(@Valid @RequestBody AvoirRequest r) { return service.creer(r); }

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTEUR','SECRETAIRE_CAISSIER','COMPTABLE','ADMINISTRATEUR')")
    public Page<AvoirResponse> lister(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "id,desc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction sens = (s.length > 1 && s[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(sens, s[0])));
    }
}