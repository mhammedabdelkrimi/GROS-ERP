package com.gros.modules.stock.entity;

import com.gros.common.exception.BusinessException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity @Table(name = "lot") @Getter @Setter @NoArgsConstructor
public class Lot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private Long produitId;
    private String numero;
    private LocalDate datePeremption;
    private int quantiteRestante;

    public void retirer(int q) {
        if (q > quantiteRestante) throw new BusinessException("Quantité supérieure au lot");
        quantiteRestante -= q;
    }
}