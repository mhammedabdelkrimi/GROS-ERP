package com.gros.modules.client.mapper;

import com.gros.modules.client.dto.ClientRequest;
import com.gros.modules.client.dto.ClientResponse;
import com.gros.modules.client.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientResponse toResponse(Client c);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actif", ignore = true)
    @Mapping(target = "bloque", ignore = true)
    Client toEntity(ClientRequest r);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actif", ignore = true)
    @Mapping(target = "bloque", ignore = true)
    void maj(ClientRequest r, @MappingTarget Client c);
}