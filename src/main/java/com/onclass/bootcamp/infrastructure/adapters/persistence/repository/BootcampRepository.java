package com.onclass.bootcamp.infrastructure.adapters.persistence.repository;

import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BootcampRepository extends ReactiveCrudRepository<BootcampEntity, Long>, CustomBootcampRepository {
    Mono<BootcampEntity> findByNombre(String nombre);
}
