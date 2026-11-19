package com.gros.modules.vente.strategy;

import com.gros.modules.client.entity.Client;
import com.gros.modules.vente.entity.LigneCommandeClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RemiseParQuantite implements StrategieRemise {
    @Override
    public BigDecimal calculer(LigneCommandeClient l, Client c) {
        if (l.getQuantite() >= 100) return BigDecimal.valueOf(5);
        if (l.getQuantite() >= 50) return BigDecimal.valueOf(3);
        return BigDecimal.ZERO;
    }
}