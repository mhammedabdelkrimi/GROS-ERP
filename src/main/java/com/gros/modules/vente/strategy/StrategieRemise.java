package com.gros.modules.vente.strategy;

import com.gros.modules.client.entity.Client;
import com.gros.modules.vente.entity.LigneCommandeClient;

import java.math.BigDecimal;

public interface StrategieRemise {
    BigDecimal calculer(LigneCommandeClient ligne, Client client);   // renvoie un pourcentage
}