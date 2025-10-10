package com.onclass.bootcamp.domain.spi;

import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadClientPort {
    Mono<Void> associateBootcampWithCapacidades(Long bootcampId, List<Long> capacidadesIds);
}
