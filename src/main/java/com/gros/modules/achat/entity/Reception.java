package com.gros.modules.achat.entity;

import com.gros.common.SecurityUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "reception") @Getter @Setter @NoArgsConstructor
public class Reception {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;
    private Long commandeId;
    private LocalDateTime dateReception = LocalDateTime.now();
    private Long creePar = SecurityUtils.idCourant();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "reception_id", nullable = false)
    private List<LigneReception> lignes = new ArrayList<>();

    public Reception(String numero, Long commandeId) {
        this.numero = numero; this.commandeId = commandeId;
    }
    public void ajouterLigne(Long produitId, int quantiteRecue, String numeroLot, LocalDate datePeremption) {
        lignes.add(new LigneReception(produitId, quantiteRecue, numeroLot, datePeremption));
    }
}