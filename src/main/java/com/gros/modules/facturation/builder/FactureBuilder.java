package com.gros.modules.facturation.builder;

import com.gros.modules.facturation.entity.FactureClient;
import com.gros.modules.facturation.entity.LigneFacture;
import com.gros.modules.livraison.entity.BonLivraison;
import com.gros.modules.vente.entity.CommandeClient;
import com.gros.modules.vente.entity.LigneCommandeClient;

import java.time.LocalDate;
import java.util.Map;

public class FactureBuilder {
    private final FactureClient f = new FactureClient();

    public FactureBuilder pourBonLivraison(BonLivraison bon, CommandeClient c, Map<Long, String> noms) {
        f.setBonLivraisonId(bon.getId());
        f.setClientId(c.getClientId());
        for (LigneCommandeClient l : c.getLignes())
            f.getLignes().add(new LigneFacture(l.getProduitId(), noms.get(l.getProduitId()), l.getQuantite(),
                    l.getPrixUnitaire(), l.getRemisePourcent(), l.getTauxTva()));
        return this;
    }
    public FactureBuilder avecNumero(String numero) { f.setNumero(numero); return this; }
    public FactureBuilder avecEcheance(LocalDate d) { f.setDateEcheance(d); return this; }
    public FactureClient construire() { f.calculerTotaux(); return f; }
}