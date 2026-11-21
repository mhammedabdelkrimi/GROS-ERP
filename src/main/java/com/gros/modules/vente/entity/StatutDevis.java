package com.gros.modules.vente.entity;

import java.util.Map;
import java.util.Set;

public enum StatutDevis {
    BROUILLON, ENVOYE, ACCEPTE, REFUSE, CONVERTI;

    private static final Map<StatutDevis, Set<StatutDevis>> TRANSITIONS = Map.of(
            BROUILLON, Set.of(ENVOYE),
            ENVOYE, Set.of(ACCEPTE, REFUSE),
            ACCEPTE, Set.of(CONVERTI, REFUSE),
            REFUSE, Set.of(),
            CONVERTI, Set.of());

    public boolean peutPasserA(StatutDevis cible) { return TRANSITIONS.get(this).contains(cible); }
}