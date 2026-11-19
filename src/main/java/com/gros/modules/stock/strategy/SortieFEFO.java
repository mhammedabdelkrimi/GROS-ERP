package com.gros.modules.stock.strategy;

import com.gros.modules.stock.entity.Lot;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component("FEFO")
public class SortieFEFO implements StrategieSortieStock {
    @Override
    public List<Lot> ordonner(List<Lot> lots) {
        return lots.stream()
                .sorted(Comparator.comparing(Lot::getDatePeremption,
                        Comparator.nullsLast(Comparator.<LocalDate>naturalOrder())))
                .toList();
    }
}