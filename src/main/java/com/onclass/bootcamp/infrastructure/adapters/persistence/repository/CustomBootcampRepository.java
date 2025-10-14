package com.onclass.bootcamp.infrastructure.adapters.persistence.repository;

import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomBootcampRepository {

    Flux<BootcampEntity> findAllByFilters(BootcampCriteria criteria);

    Mono<Long> countByFilters(BootcampCriteria criteria);
}
