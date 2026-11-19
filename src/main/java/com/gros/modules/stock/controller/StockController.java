package com.gros.modules.stock.controller;

import com.gros.modules.stock.dto.AlerteStockResponse;
import com.gros.modules.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @RequestMapping("/api/stock") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MAGASINIER','RESPONSABLE_ACHATS','DIRECTEUR','ADMINISTRATEUR')")
public class StockController {
    private final StockService service;

    @GetMapping("/alertes")
    public List<AlerteStockResponse> alertes() { return service.alertes(); }

    @GetMapping("/{produitId}")
    public long quantite(@PathVariable Long produitId) { return service.quantiteDisponible(produitId); }
}