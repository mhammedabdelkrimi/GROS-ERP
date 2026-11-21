package com.gros.modules.achat.dto;

import com.gros.modules.achat.entity.PieceJointeFactureFournisseur;

import java.time.LocalDateTime;

public record PieceJointeResponse(Long id, Long factureId, String nomFichier, String typeContenu,
                                  int taille, String empreinte, LocalDateTime dateCreation) {
    public static PieceJointeResponse de(PieceJointeFactureFournisseur p) {
        return new PieceJointeResponse(p.getId(), p.getFactureId(), p.getNomFichier(), p.getTypeContenu(),
                p.getTaille(), p.getEmpreinte(), p.getDateCreation());
    }
}