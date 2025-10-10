package com.onclass.bootcamp.infrastructure.entrypoints.mapper;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BootcampMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "nombre", target = "nombre")
    @Mapping(source = "descripcion", target = "descripcion")
    @Mapping(source = "fechaLanzamiento", target = "fechaLanzamiento")
    @Mapping(source = "duracion", target = "duracion")
    @Mapping(source = "capacidades", target = "capacidades")
    Bootcamp toModel(BootcampDTO dto);

    BootcampDTO toDTO(Bootcamp model);
}
