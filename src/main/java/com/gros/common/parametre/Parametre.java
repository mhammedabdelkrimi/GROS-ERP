package com.gros.common.parametre;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Table(name = "parametre") @Getter @NoArgsConstructor
public class Parametre {
    @Id private String cle;
    private String valeur;
}