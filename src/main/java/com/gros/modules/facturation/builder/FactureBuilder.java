package com.gros.modules.facturation.builder;

import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.facturation.entity.LigneFacture;
import com.gros.modules.livraison.entity.BonLivraison;
import com.gros.modules.vente.entity.CommandeClient;
import com.gros.modules.vente.entity.LigneCommandeClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class FactureBuilder {
    private final FactureClient f = new FactureClient();

    public FactureBuilder pourBonLivraison(BonLivraison bon, CommandeClient c, Map<Long, String> noms) {
        return pourBonLivraison(bon, c, noms, Map.of());
    }

    /** couts : produitId -> prix d'achat du moment, figé dans la ligne de facture (marge exacte pour toujours). */
    public FactureBuilder pourBonLivraison(BonLivraison bon, CommandeClient c, Map<Long, String> noms,
                                           Map<Long, BigDecimal> couts) {
        f.setBonLivraisonId(bon.getId());
        f.setClientId(c.getClientId());
        for (LigneCommandeClient l : c.getLignes())
            f.getLignes().add(new LigneFacture(l.getProduitId(), noms.get(l.getProduitId()), l.getQuantite(),
                    l.getPrixUnitaire(), l.getRemisePourcent(), l.getTauxTva(),
                    couts.getOrDefault(l.getProduitId(), BigDecimal.ZERO)));
        return this;
    }
    public FactureBuilder avecNumero(String numero) { f.setNumero(numero); return this; }
    public FactureBuilder avecEcheance(LocalDate d) { f.setDateEcheance(d); return this; }
    public FactureClient construire() { f.calculerTotaux(); return f; }
}