package com.gros.modules.achat.controller;

import com.gros.modules.achat.dto.FactureFournisseurRequest;
import com.gros.modules.achat.dto.FactureFournisseurResponse;
import com.gros.modules.achat.dto.PaiementFournisseurRequest;
import com.gros.modules.achat.entity.StatutFactureFournisseur;
import com.gros.modules.achat.service.FactureFournisseurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

@RestController @RequestMapping("/api/factures-fournisseurs") @RequiredArgsConstructor
public class FactureFournisseurController {
    private static final String LECTURE = "hasAnyRole('COMPTABLE','RESPONSABLE_ACHATS','DIRECTEUR','ADMINISTRATEUR')";
    private final FactureFournisseurService service;

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('COMPTABLE','RESPONSABLE_ACHATS','ADMINISTRATEUR')")
    public FactureFournisseurResponse creer(@Valid @RequestBody FactureFournisseurRequest r) {
        return service.creer(r);
    }

    @PostMapping("/{id}/paiements")
    @PreAuthorize("hasAnyRole('COMPTABLE','DIRECTEUR','ADMINISTRATEUR')")
    public FactureFournisseurResponse payer(@PathVariable Long id, @Valid @RequestBody PaiementFournisseurRequest r) {
        return service.payer(id, r);
    }

    @GetMapping("/{id}")
    @PreAuthorize(LECTURE)
    public FactureFournisseurResponse consulter(@PathVariable Long id) { return service.consulter(id); }

    @GetMapping
    @PreAuthorize(LECTURE)
    public Page<FactureFournisseurResponse> lister(@RequestParam(required = false) StatutFactureFournisseur statut,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "id,desc") String sort) {
        String[] s = sort.split(",");
        Sort.Direction sens = (s.length > 1 && s[1].equalsIgnoreCase("asc")) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return service.lister(statut, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by(sens, s[0])));
    }
}