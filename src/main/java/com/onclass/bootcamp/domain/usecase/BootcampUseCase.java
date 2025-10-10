package com.onclass.bootcamp.domain.usecase;

import com.onclass.bootcamp.domain.api.BootcampServicePort;
import com.onclass.bootcamp.domain.constants.Constants;
import com.onclass.bootcamp.domain.enums.TechnicalMessage;
import com.onclass.bootcamp.domain.exceptions.BusinessException;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.spi.BootcampPersistencePort;
import com.onclass.bootcamp.domain.spi.CapacidadClientPort;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BootcampUseCase implements BootcampServicePort {

    private final BootcampPersistencePort bootcampPersistencePort;
    private final CapacidadClientPort capacidadClientPort;

    public BootcampUseCase(BootcampPersistencePort bootcampPersistencePort,
                           CapacidadClientPort capacidadClientPort) {
        this.bootcampPersistencePort = bootcampPersistencePort;
        this.capacidadClientPort = capacidadClientPort;
    }

    @Override
    public Mono<Bootcamp> registrarBootcamp(Bootcamp bootcamp, String messageId) {

        if (bootcamp.nombre() == null || bootcamp.nombre().isBlank()) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_NOMBRE_REQUIRED));
        }
        if (bootcamp.nombre().length() > Constants.MAX_NOMBRE_BOOTCAMP) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_NOMBRE_TOO_LONG));
        }

        if (bootcamp.descripcion() == null || bootcamp.descripcion().isBlank()) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_DESCRIPCION_REQUIRED));
        }
        if (bootcamp.descripcion().length() > Constants.MAX_DESCRIPCION_BOOTCAMP) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_DESCRIPCION_TOO_LONG));
        }

        if (bootcamp.fechaLanzamiento() == null) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_FECHA_REQUIRED));
        }
        if (bootcamp.fechaLanzamiento().isBefore(LocalDate.now())) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_FECHA_INVALID));
        }

        if (bootcamp.duracion() == null || bootcamp.duracion() <= 0) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_DURACION_REQUIRED));
        }

        List<Long> capacidades = bootcamp.capacidades();
        if (capacidades == null || capacidades.size() < Constants.MIN_CAPACIDADES) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_MIN_CAPACIDADES));
        }
        if (capacidades.size() > Constants.MAX_CAPACIDADES) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_MAX_CAPACIDADES));
        }

        Set<Long> set = new HashSet<>(capacidades);
        if (set.size() != capacidades.size()) {
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_CAPACIDADES_DUPLICADAS));
        }

        return bootcampPersistencePort.existByNombre(bootcamp.nombre())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_ALREADY_EXISTS));
                    }

                    return bootcampPersistencePort.saveBootcamp(bootcamp)
                            .flatMap(saved ->
                                    capacidadClientPort
                                            .associateBootcampWithCapacidades(saved.id(), capacidades)
                                            .thenReturn(saved)
                            );
                });
    }
}
