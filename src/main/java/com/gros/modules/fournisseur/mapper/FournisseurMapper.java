package com.gros.modules.fournisseur.mapper;

import com.gros.modules.fournisseur.dto.FournisseurRequest;
import com.gros.modules.fournisseur.dto.FournisseurResponse;
import com.gros.modules.fournisseur.entity.Fournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FournisseurMapper {
    FournisseurResponse toResponse(Fournisseur f);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actif", ignore = true)
    Fournisseur toEntity(FournisseurRequest r);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actif", ignore = true)
    void maj(FournisseurRequest r, @MappingTarget Fournisseur f);
}