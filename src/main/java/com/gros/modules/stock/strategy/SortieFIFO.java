package com.gros.modules.stock.strategy;

import com.gros.modules.stock.entity.Lot;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component("FIFO")
public class SortieFIFO implements StrategieSortieStock {
    @Override
    public List<Lot> ordonner(List<Lot> lots) {
        return lots.stream().sorted(Comparator.comparing(Lot::getId)).toList();
    }
}