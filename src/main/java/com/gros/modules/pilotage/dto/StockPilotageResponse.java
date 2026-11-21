package com.gros.modules.pilotage.dto;

import com.gros.modules.stock.dto.AlerteStockResponse;

import java.math.BigDecimal;
import java.util.List;

public record StockPilotageResponse(BigDecimal valeurStock, int nombreProduitsEnStock,
                                    List<AlerteStockResponse> alertes) {}