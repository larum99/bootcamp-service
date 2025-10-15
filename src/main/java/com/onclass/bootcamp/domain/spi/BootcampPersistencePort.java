package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampList;
import com.onclass.bootcamp.domain.utils.PageResult;
import reactor.core.publisher.Mono;

public interface BootcampPersistencePort {
    Mono<Bootcamp> saveBootcamp(Bootcamp bootcamp);
    Mono<Boolean> existByNombre(String nombre);
    Mono<PageResult<BootcampList>> findAll(BootcampCriteria criteria);
}
