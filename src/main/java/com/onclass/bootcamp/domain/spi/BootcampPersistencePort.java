package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.domain.model.Bootcamp;
import reactor.core.publisher.Mono;

public interface BootcampPersistencePort {
    Mono<Bootcamp> saveBootcamp(Bootcamp bootcamp);
    Mono<Boolean> existByNombre(String nombre);
}
