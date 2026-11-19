package com.gros.modules.livraison.entity;

import com.gros.common.exception.BusinessException;
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

@Entity @Table(name = "bon_livraison") @Getter @Setter @NoArgsConstructor
public class BonLivraison {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;
    private Long commandeId;
    @Enumerated(EnumType.STRING) private StatutBon statut = StatutBon.CREE;
    private boolean signeParClient;
    private LocalDateTime dateSignature;
    private String motifRefus;

    public BonLivraison(String numero, Long commandeId) {
        this.numero = numero; this.commandeId = commandeId;
    }

    private void verifierEnCours() {
        if (statut != StatutBon.CREE) throw new BusinessException("Bon déjà traité");
    }

    public void faireSigner() {
        verifierEnCours();
        signeParClient = true;
        dateSignature = LocalDateTime.now();
    }

    public void confirmer() {
        verifierEnCours();
        if (!signeParClient) throw new BusinessException("Le client doit signer le bon de livraison");
        statut = StatutBon.CONFIRME;
    }

    public void refuser(String motif) {
        verifierEnCours();
        statut = StatutBon.REFUSE;
        motifRefus = motif;
    }
}