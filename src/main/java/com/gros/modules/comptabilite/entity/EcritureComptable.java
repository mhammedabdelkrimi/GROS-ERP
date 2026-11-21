package com.gros.modules.comptabilite.entity;

import com.gros.common.SecurityUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Écriture comptable : jamais modifiée ni supprimée (BNF-12). Une correction = une écriture d'extourne. */
@Entity @Table(name = "ecriture_comptable") @Immutable
@Getter @Setter @NoArgsConstructor
public class EcritureComptable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;
    private LocalDate dateEcriture = LocalDate.now();
    private String libelle;
    private String pieceType;      // BNF-18 : pièce d'origine
    private Long pieceId;
    private Long extourneDe;       // non nul = cette écriture extourne l'écriture indiquée
    private Long creePar = SecurityUtils.idCourant();
    private LocalDateTime dateCreation = LocalDateTime.now();

    public EcritureComptable(String numero, String libelle, String pieceType, Long pieceId, Long extourneDe) {
        this.numero = numero;
        this.libelle = libelle;
        this.pieceType = pieceType;
        this.pieceId = pieceId;
        this.extourneDe = extourneDe;
    }
}