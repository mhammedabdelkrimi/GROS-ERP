package com.gros.common.parametre;

import com.gros.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service @RequiredArgsConstructor
public class ParametreService {
    private final ParametreRepository repo;

    public BigDecimal decimal(String cle) {
        return new BigDecimal(repo.findById(cle)
                .orElseThrow(() -> new NotFoundException("Paramètre absent : " + cle)).getValeur());
    }
}