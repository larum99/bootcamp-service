package com.onclass.bootcamp.infrastructure.adapters.persistence.mapper;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BootcampEntityMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "fechaLanzamiento", source = "fechaLanzamiento")
    @Mapping(target = "duracion", source = "duracion")
    Bootcamp toModel(BootcampEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "fechaLanzamiento", source = "fechaLanzamiento")
    @Mapping(target = "duracion", source = "duracion")
    BootcampEntity toEntity(Bootcamp bootcamp);
}
