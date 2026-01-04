package com.onclass.bootcamp.infrastructure.entrypoints.mapper;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampDTO;
import com.onclass.bootcamp.infrastructure.entrypoints.util.MapperConstants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = MapperConstants.SPRING_COMPONENT_MODEL)
public interface BootcampMapper {

    @Mapping(target = MapperConstants.ID_FIELD, ignore = true)
    @Mapping(source = MapperConstants.NOMBRE_FIELD, target = MapperConstants.NOMBRE_FIELD)
    @Mapping(source = MapperConstants.DESCRIPCION_FIELD, target = MapperConstants.DESCRIPCION_FIELD)
    @Mapping(source = MapperConstants.FECHA_LANZAMIENTO_FIELD, target = MapperConstants.FECHA_LANZAMIENTO_FIELD)
    @Mapping(source = MapperConstants.DURACION_FIELD, target = MapperConstants.DURACION_FIELD)
    @Mapping(source = MapperConstants.CAPACIDADES_FIELD, target = MapperConstants.CAPACIDADES_FIELD)
    Bootcamp toModel(BootcampDTO dto);

    BootcampDTO toDTO(Bootcamp model);
}
