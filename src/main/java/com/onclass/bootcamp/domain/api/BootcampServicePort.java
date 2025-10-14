package com.onclass.bootcamp.domain.api;

import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.utils.PageResult;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampListDTO;
import reactor.core.publisher.Mono;

public interface BootcampServicePort {

    Mono<Bootcamp> registrarBootcamp(Bootcamp bootcamp, String messageId);
    Mono<PageResult<BootcampListDTO>> listarBootcamps(BootcampCriteria criteria);
}
