package com.gros.modules.achat.entity;

import com.gros.common.SecurityUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name = "piece_jointe_facture_fournisseur") @Getter @Setter @NoArgsConstructor
public class PieceJointeFactureFournisseur {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long factureId;
    private String nomFichier;
    private String typeContenu;
    private int taille;
    private String empreinte;      // SHA-256 : preuve que le fichier n'a pas été altéré
    @Lob private byte[] contenu;
    private Long creePar = SecurityUtils.idCourant();
    private LocalDateTime dateCreation = LocalDateTime.now();
}