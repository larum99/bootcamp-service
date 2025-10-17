package com.onclass.bootcamp.domain.spi;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TecnologiaClientPort {
    Mono<Void> eliminarTecnologiasPorCapacidades(List<Long> capacidadIds);
    Mono<Integer> countCapacidadesByTecnologiaId(Long tecnologiaId);
    Mono<Void> eliminarTecnologiaPorId(Long tecnologiaId);
    Flux<Long> findTecnologiaIdsByCapacidades(List<Long> capacidadIds);
}
