package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.domain.utils.CapacidadSummary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadClientPort {
    Mono<Void> associateBootcampWithCapacidades(Long bootcampId, List<Long> capacidadesIds);
    Flux<CapacidadSummary> findCapacidadesByBootcampId(Long bootcampId);
    Mono<List<Long>> eliminarCapacidadesPorBootcamp(Long bootcampId);
}
