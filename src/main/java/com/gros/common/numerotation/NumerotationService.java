package com.gros.common.numerotation;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NumerotationService {

    private final CompteurRepository repo;

    @Transactional   // rejoint la transaction du document : tout réussit ou tout est annulé
    public String prochainNumero(String type) {
        int annee = LocalDate.now().getYear();
        Compteur c = repo.findByTypeDocAndAnnee(type, annee)
                .orElseGet(() -> repo.saveAndFlush(new Compteur(type, annee)));
        c.setDerniereValeur(c.getDerniereValeur() + 1);
        return String.format("%s-%d-%06d", type, annee, c.getDerniereValeur());   // FAC-2026-000001
    }
}