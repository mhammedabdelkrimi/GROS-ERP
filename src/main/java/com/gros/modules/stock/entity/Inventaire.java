package com.gros.modules.stock.entity;

import com.gros.common.SecurityUtils;
import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "inventaire") @Getter @Setter @NoArgsConstructor
public class Inventaire {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String numero;
    private LocalDate dateInventaire = LocalDate.now();
    private boolean cloture;
    private Long creePar = SecurityUtils.idCourant();
    private Long cloturePar;
    private LocalDateTime dateCloture;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inventaire_id", nullable = false)
    @OrderBy("produitId")
    private List<LigneInventaire> lignes = new ArrayList<>();

    public void ajouterLigne(Long produitId, int quantiteTheorique) {
        lignes.add(new LigneInventaire(produitId, quantiteTheorique));
    }

    /** BNF-12 : un inventaire clôturé n'est plus modifiable. */
    public void verifierOuvert() {
        if (cloture) throw new BusinessException("Inventaire déjà clôturé : il ne peut plus être modifié");
    }

    public LigneInventaire ligne(Long produitId) {
        return lignes.stream().filter(l -> l.getProduitId().equals(produitId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Le produit " + produitId + " ne fait pas partie de cet inventaire"));
    }

    /** Tout produit doit être compté, et tout écart doit être justifié avant la clôture. */
    public void verifierPretACloturer() {
        for (LigneInventaire l : lignes) {
            if (l.getQuantiteComptee() == null)
                throw new BusinessException("Produit " + l.getProduitId() + " : quantité non comptée");
            Integer ecart = l.ecart();
            boolean sansJustification = l.getJustification() == null || l.getJustification().isBlank();
            if (ecart != null && ecart != 0 && sansJustification)
                throw new BusinessException("Produit " + l.getProduitId() + " : écart de " + ecart + " à justifier");
        }
    }

    public void cloturer(Long utilisateurId) {
        verifierOuvert();
        verifierPretACloturer();
        cloture = true;
        cloturePar = utilisateurId;
        dateCloture = LocalDateTime.now();
    }
}