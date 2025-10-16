package com.onclass.bootcamp.domain.spi;

import reactor.core.publisher.Mono;

import java.util.List;

public interface TecnologiaClientPort {
    Mono<Void> eliminarTecnologiasPorCapacidades(List<Long> capacidadIds);
}
