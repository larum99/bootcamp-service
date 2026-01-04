package com.onclass.bootcamp.domain.spi;

import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampReporteDTO;
import reactor.core.publisher.Mono;

public interface BootcampReporteClientPort {
    Mono<Void> notificarNuevoBootcamp(BootcampReporteDTO reporteDTO);
}
