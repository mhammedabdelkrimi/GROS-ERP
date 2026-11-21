package com.gros.modules.comptabilite.service;

import com.gros.modules.comptabilite.dto.TotauxCompte;
import com.gros.modules.comptabilite.dto.TvaResponse;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public final class CalculTva {
    public static final String TVA_RECUPERABLE = "3455";

    private CalculTva() {}

    /** TVA facturée = crédit - débit du 4455. TVA récupérable = débit - crédit du 3455. */
    public static TvaResponse calculer(YearMonth periode, List<TotauxCompte> totaux, boolean cloturee) {
        BigDecimal facturee = BigDecimal.ZERO;
        BigDecimal recuperable = BigDecimal.ZERO;
        for (TotauxCompte t : totaux) {
            if (SchemaComptable.TVA_FACTUREE.equals(t.compteNumero()))
                facturee = t.credit().subtract(t.debit());
            else if (TVA_RECUPERABLE.equals(t.compteNumero()))
                recuperable = t.debit().subtract(t.credit());
        }
        return new TvaResponse(periode.getYear(), periode.getMonthValue(),
                periode.atDay(1), periode.atEndOfMonth(),
                facturee, recuperable, facturee.subtract(recuperable), cloturee);
    }
}