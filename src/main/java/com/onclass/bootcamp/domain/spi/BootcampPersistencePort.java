package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.utils.PageResult;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampListDTO;
import reactor.core.publisher.Mono;

public interface BootcampPersistencePort {
    Mono<Bootcamp> saveBootcamp(Bootcamp bootcamp);
    Mono<Boolean> existByNombre(String nombre);
    Mono<PageResult<BootcampListDTO>> findAll(BootcampCriteria criteria);
}
