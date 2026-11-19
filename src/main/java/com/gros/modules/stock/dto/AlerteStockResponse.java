package com.gros.modules.stock.dto;

public record AlerteStockResponse(Long produitId, String reference, String designation,
                                  long quantiteDisponible, int seuilAlerte) {}