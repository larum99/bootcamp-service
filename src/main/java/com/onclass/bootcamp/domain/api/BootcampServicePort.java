package com.onclass.bootcamp.domain.api;

import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampList;
import com.onclass.bootcamp.domain.utils.PageResult;
import reactor.core.publisher.Mono;

public interface BootcampServicePort {

    Mono<Bootcamp> registrarBootcamp(Bootcamp bootcamp, String messageId);
    Mono<PageResult<BootcampList>> listarBootcamps(BootcampCriteria criteria);
    Mono<Void> eliminarBootcamp(Long bootcampId);
    Mono<BootcampList> obtenerBootcampPorId(Long id);
}
