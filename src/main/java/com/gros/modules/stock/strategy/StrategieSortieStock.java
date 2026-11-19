package com.gros.modules.stock.strategy;

import com.gros.modules.stock.entity.Lot;

import java.util.List;

public interface StrategieSortieStock {
    List<Lot> ordonner(List<Lot> lots);
}