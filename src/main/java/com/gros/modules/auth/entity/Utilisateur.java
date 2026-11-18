package com.gros.modules.auth.entity;

import jakarta.persistence.Column;
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

@Entity
@Table(name = "utilisateur")
@Getter
@Setter
@NoArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String email;
    private String motDePasseHache;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_nom")
    private Role role;

    private boolean actif = true;
    private int tentativesEchouees;
    private LocalDateTime verrouilleJusqua;

    public boolean estVerrouille() {
        return verrouilleJusqua != null && verrouilleJusqua.isAfter(LocalDateTime.now());
    }

    public void enregistrerEchec() {
        tentativesEchouees++;
        if (tentativesEchouees >= 5) {
            verrouilleJusqua = LocalDateTime.now().plusMinutes(15);
            tentativesEchouees = 0;
        }
    }

    public void reinitialiserEchecs() {
        tentativesEchouees = 0;
        verrouilleJusqua = null;
    }
}