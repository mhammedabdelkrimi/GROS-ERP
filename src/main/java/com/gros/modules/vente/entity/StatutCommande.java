package com.gros.modules.vente.entity;

import java.util.Map;
import java.util.Set;

public enum StatutCommande {
    BROUILLON, VALIDEE, EN_PREPARATION, LIVREE, FACTUREE, ANNULEE;

    private static final Map<StatutCommande, Set<StatutCommande>> TRANSITIONS = Map.of(
            BROUILLON, Set.of(VALIDEE, ANNULEE),
            VALIDEE, Set.of(EN_PREPARATION, ANNULEE),
            EN_PREPARATION, Set.of(LIVREE, ANNULEE),
            LIVREE, Set.of(FACTUREE),
            FACTUREE, Set.of(),
            ANNULEE, Set.of());

    public boolean peutPasserA(StatutCommande cible) {
        return TRANSITIONS.get(this).contains(cible);
    }
}