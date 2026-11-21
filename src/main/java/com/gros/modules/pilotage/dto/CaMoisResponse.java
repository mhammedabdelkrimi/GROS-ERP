package com.gros.modules.pilotage.dto;

import java.math.BigDecimal;

/** variationPourcent : CA net HT du mois comparé au mois précédent. Null si pas de comparaison possible. */
public record CaMoisResponse(int mois, BigDecimal caHt, BigDecimal avoirsHt, BigDecimal caNetHt,
                             BigDecimal caTtc, BigDecimal avoirsTtc, BigDecimal caNetTtc,
                             BigDecimal variationPourcent) {}