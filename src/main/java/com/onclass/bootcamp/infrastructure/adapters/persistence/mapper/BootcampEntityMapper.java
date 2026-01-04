package com.onclass.bootcamp.infrastructure.adapters.persistence.mapper;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampList;
import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampEntity;
import com.onclass.bootcamp.infrastructure.adapters.util.MapperConstants;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampListDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = MapperConstants.SPRING_COMPONENT_MODEL)
public interface BootcampEntityMapper {
    @Mapping(target = MapperConstants.ID_FIELD, source = MapperConstants.ID_FIELD)
    @Mapping(target = MapperConstants.NOMBRE_FIELD, source = MapperConstants.NOMBRE_FIELD)
    @Mapping(target = MapperConstants.DESCRIPCION_FIELD, source = MapperConstants.DESCRIPCION_FIELD)
    @Mapping(target = MapperConstants.FECHA_LANZAMIENTO_FIELD, source = MapperConstants.FECHA_LANZAMIENTO_FIELD)
    @Mapping(target = MapperConstants.DURACION_FIELD, source = MapperConstants.DURACION_FIELD)
    Bootcamp toModel(BootcampEntity entity);

    @Mapping(target = MapperConstants.ID_FIELD, source = MapperConstants.ID_FIELD)
    @Mapping(target = MapperConstants.NOMBRE_FIELD, source = MapperConstants.NOMBRE_FIELD)
    @Mapping(target = MapperConstants.DESCRIPCION_FIELD, source = MapperConstants.DESCRIPCION_FIELD)
    @Mapping(target = MapperConstants.FECHA_LANZAMIENTO_FIELD, source = MapperConstants.FECHA_LANZAMIENTO_FIELD)
    @Mapping(target = MapperConstants.DURACION_FIELD, source = MapperConstants.DURACION_FIELD)
    BootcampEntity toEntity(Bootcamp bootcamp);

    @Mapping(target = MapperConstants.CAPACIDADES_FIELD, ignore = true)
    BootcampList toListModel(BootcampEntity entity);
}
