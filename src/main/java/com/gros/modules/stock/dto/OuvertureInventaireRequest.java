package com.gros.modules.stock.dto;

import java.util.List;

public record OuvertureInventaireRequest(List<Long> produitIds) {}