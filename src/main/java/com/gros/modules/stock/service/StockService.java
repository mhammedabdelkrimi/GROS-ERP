package com.gros.modules.stock.service;

import com.gros.common.exception.BusinessException;
import com.gros.common.exception.NotFoundException;
import com.gros.modules.produit.entity.Produit;
import com.gros.modules.produit.repository.ProduitRepository;
import com.gros.modules.stock.dto.AlerteStockResponse;
import com.gros.modules.stock.entity.Lot;
import com.gros.modules.stock.entity.MouvementStock;
import com.gros.modules.stock.entity.TypeMouvement;
import com.gros.modules.stock.repository.LotRepository;
import com.gros.modules.stock.repository.MouvementStockRepository;
import com.gros.modules.stock.strategy.StrategieSortieStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service @RequiredArgsConstructor
public class StockService {
    private final LotRepository lots;
    private final MouvementStockRepository mouvements;
    private final ProduitRepository produits;
    private final Map<String, StrategieSortieStock> strategies;   // Spring injecte FIFO et FEFO

    @Transactional
    public void enregistrerEntree(Long produitId, int quantite, String numeroLot, LocalDate peremption,
                                  String docType, Long docId) {
        Lot lot = new Lot();
        lot.setProduitId(produitId);
        lot.setNumero(numeroLot);
        lot.setDatePeremption(peremption);
        lot.setQuantiteRestante(quantite);
        lots.save(lot);
        mouvements.save(new MouvementStock(produitId, lot.getId(), TypeMouvement.ENTREE_ACHAT, quantite, docType, docId));
    }

    @Transactional
    public void enregistrerSortie(Long produitId, int quantite, TypeMouvement type, String docType, Long docId) {
        Produit p = produits.findById(produitId).orElseThrow(() -> new NotFoundException("Produit introuvable"));
        StrategieSortieStock strategie = strategies.get(p.isPerissable() ? "FEFO" : "FIFO");
        List<Lot> dispo = strategie.ordonner(lots.disponiblesAvecVerrou(produitId));
        int total = dispo.stream().mapToInt(Lot::getQuantiteRestante).sum();
        if (total < quantite)
            throw new BusinessException("Stock insuffisant pour " + p.getDesignation() + " : " + total + " disponibles");
        int reste = quantite;
        for (Lot l : dispo) {
            if (reste == 0) break;
            int pris = Math.min(reste, l.getQuantiteRestante());
            l.retirer(pris);
            mouvements.save(new MouvementStock(produitId, l.getId(), type, -pris, docType, docId));
            reste -= pris;
        }
    }

    @Transactional(readOnly = true)
    public long quantiteDisponible(Long produitId) {
        return mouvements.quantiteDisponible(produitId);
    }

    @Transactional(readOnly = true)
    public List<AlerteStockResponse> alertes() {
        return produits.findByActifTrue().stream()
                .map(p -> new AlerteStockResponse(p.getId(), p.getReference(), p.getDesignation(),
                        mouvements.quantiteDisponible(p.getId()), p.getSeuilAlerte()))
                .filter(a -> a.quantiteDisponible() <= a.seuilAlerte())
                .toList();
    }
}