package com.gros.modules.stock.entity;

import com.gros.common.SecurityUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity @Table(name = "mouvement_stock") @Getter @Setter @NoArgsConstructor
public class MouvementStock {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long produitId;
    private Long lotId;
    @Enumerated(EnumType.STRING) private TypeMouvement typeMouvement;
    private int quantite;
    private String documentType;
    private Long documentId;
    private LocalDateTime dateMouvement = LocalDateTime.now();
    private Long auteurId = SecurityUtils.idCourant();

    public MouvementStock(Long produitId, Long lotId, TypeMouvement t, int q, String docType, Long docId) {
        this.produitId = produitId; this.lotId = lotId; this.typeMouvement = t;
        this.quantite = q; this.documentType = docType; this.documentId = docId;
    }
}