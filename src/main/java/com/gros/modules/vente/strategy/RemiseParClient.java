package com.gros.modules.vente.strategy;

import com.gros.modules.client.entity.Client;
import com.gros.modules.vente.entity.LigneCommandeClient;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RemiseParClient implements StrategieRemise {
    @Override
    public BigDecimal calculer(LigneCommandeClient ligne, Client client) {
        return client.getRemiseHabituelle() == null ? BigDecimal.ZERO : client.getRemiseHabituelle();
    }
}