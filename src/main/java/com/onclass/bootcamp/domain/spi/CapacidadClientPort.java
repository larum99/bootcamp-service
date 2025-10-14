package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.infrastructure.entrypoints.dto.CapacidadSummaryDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacidadClientPort {
    Mono<Void> associateBootcampWithCapacidades(Long bootcampId, List<Long> capacidadesIds);
    Flux<CapacidadSummaryDTO> findCapacidadesByBootcampId(Long bootcampId);
}
