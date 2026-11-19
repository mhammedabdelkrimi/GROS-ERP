package com.gros.modules.vente.service;

import com.gros.modules.client.entity.Client;
import com.gros.modules.vente.entity.LigneCommandeClient;
import com.gros.modules.vente.strategy.StrategieRemise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service @RequiredArgsConstructor
public class RemiseService {
    private final List<StrategieRemise> strategies;   // Spring injecte toutes les stratégies

    public BigDecimal meilleureRemise(LigneCommandeClient l, Client c) {
        return strategies.stream().map(s -> s.calculer(l, c)).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
    }
}