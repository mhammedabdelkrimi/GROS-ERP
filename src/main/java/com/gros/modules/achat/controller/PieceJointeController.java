package com.gros.modules.achat.controller;

import com.gros.modules.achat.dto.PieceJointeResponse;
import com.gros.modules.achat.entity.PieceJointeFactureFournisseur;
import com.gros.modules.achat.service.PieceJointeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController @RequestMapping("/api/factures-fournisseurs") @RequiredArgsConstructor
public class PieceJointeController {
    private final PieceJointeService service;

    @PostMapping(value = "/{id}/piece-jointe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('COMPTABLE','RESPONSABLE_ACHATS','ADMINISTRATEUR')")
    public PieceJointeResponse ajouter(@PathVariable Long id, @RequestParam("fichier") MultipartFile fichier) {
        return service.ajouter(id, fichier);
    }

    @GetMapping("/{id}/piece-jointe")
    @PreAuthorize("hasAnyRole('COMPTABLE','RESPONSABLE_ACHATS','DIRECTEUR','ADMINISTRATEUR')")
    public ResponseEntity<byte[]> telecharger(@PathVariable Long id) {
        PieceJointeFactureFournisseur p = service.telecharger(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(p.getTypeContenu()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(p.getNomFichier(), StandardCharsets.UTF_8).build().toString())
                .body(p.getContenu());
    }
}