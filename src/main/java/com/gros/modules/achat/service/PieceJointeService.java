package com.gros.modules.achat.service;

import com.gros.common.audit.AuditService;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.modules.achat.dto.PieceJointeResponse;
import com.gros.modules.achat.entity.PieceJointeFactureFournisseur;
import com.gros.modules.achat.repository.FactureFournisseurRepository;
import com.gros.modules.achat.repository.PieceJointeFactureFournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service @RequiredArgsConstructor
public class PieceJointeService {
    private static final int TAILLE_MAX = 5 * 1024 * 1024;

    private final PieceJointeFactureFournisseurRepository pieces;
    private final FactureFournisseurRepository factures;
    private final AuditService audit;

    /** Une seule pièce par facture, non remplaçable (la preuve d'origine reste intacte). */
    @Transactional
    public PieceJointeResponse ajouter(Long factureId, MultipartFile fichier) {
        factures.findByIdAvecVerrou(factureId)          // verrou : pas de double dépôt simultané
                .orElseThrow(() -> new NotFoundException("Facture fournisseur introuvable : " + factureId));
        if (pieces.existsByFactureId(factureId))
            throw new BusinessException("Une pièce jointe existe déjà pour cette facture : elle ne peut pas être remplacée");
        if (fichier == null || fichier.isEmpty())
            throw new BusinessException("Fichier vide ou absent");
        if (fichier.getSize() > TAILLE_MAX)
            throw new BusinessException("Fichier trop volumineux (5 Mo maximum)");
        byte[] contenu;
        try {
            contenu = fichier.getBytes();
        } catch (IOException e) {
            throw new BusinessException("Lecture du fichier impossible");
        }
        String type = TypeFichier.detecter(contenu)
                .orElseThrow(() -> new BusinessException("Format non accepté : PDF, PNG ou JPEG uniquement"));

        PieceJointeFactureFournisseur p = new PieceJointeFactureFournisseur();
        p.setFactureId(factureId);
        p.setNomFichier(TypeFichier.nettoyerNom(fichier.getOriginalFilename()));
        p.setTypeContenu(type);
        p.setTaille(contenu.length);
        p.setEmpreinte(TypeFichier.empreinteSha256(contenu));
        p.setContenu(contenu);
        pieces.save(p);
        audit.tracer("AJOUT_PIECE", "FACTURE_FOURNISSEUR", factureId);
        return PieceJointeResponse.de(p);
    }

    @Transactional(readOnly = true)
    public PieceJointeFactureFournisseur telecharger(Long factureId) {
        return pieces.findByFactureId(factureId)
                .orElseThrow(() -> new NotFoundException("Aucune pièce jointe pour la facture " + factureId));
    }
}