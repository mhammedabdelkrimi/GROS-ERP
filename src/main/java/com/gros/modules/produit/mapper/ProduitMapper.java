package com.gros.modules.produit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.gros.modules.produit.dto.HistoriquePrixResponse;
import com.gros.modules.produit.dto.ProduitRequest;
import com.gros.modules.produit.dto.ProduitResponse;
import com.gros.modules.produit.entity.HistoriquePrix;
import com.gros.modules.produit.entity.Produit;

@Mapper(componentModel = "spring")
public interface ProduitMapper {

    ProduitResponse toResponse(Produit p);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actif", ignore = true)
    Produit toEntity(ProduitRequest r);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actif", ignore = true)
    void maj(ProduitRequest r, @MappingTarget Produit p);

    HistoriquePrixResponse toResponse(HistoriquePrix h);
}