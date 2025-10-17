package com.onclass.bootcamp.domain.usecase;

import com.onclass.bootcamp.domain.api.BootcampServicePort;
import com.onclass.bootcamp.domain.constants.Constants;
import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.domain.enums.TechnicalMessage;
import com.onclass.bootcamp.domain.exceptions.BusinessException;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampList;
import com.onclass.bootcamp.domain.spi.BootcampPersistencePort;
import com.onclass.bootcamp.domain.spi.CapacidadClientPort;
import com.onclass.bootcamp.domain.spi.TecnologiaClientPort;
import com.onclass.bootcamp.domain.utils.CapacidadSummary;
import com.onclass.bootcamp.domain.utils.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;

public class BootcampUseCase implements BootcampServicePort {
    private static final Logger log = LoggerFactory.getLogger(BootcampUseCase.class);

    private final BootcampPersistencePort bootcampPersistencePort;
    private final CapacidadClientPort capacidadClientPort;
    private final TecnologiaClientPort tecnologiaClientPort;

    public BootcampUseCase(BootcampPersistencePort bootcampPersistencePort,
                           CapacidadClientPort capacidadClientPort, TecnologiaClientPort tecnologiaClientPort) {
        this.bootcampPersistencePort = bootcampPersistencePort;
        this.capacidadClientPort = capacidadClientPort;
        this.tecnologiaClientPort = tecnologiaClientPort;
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

    @Override
    public Mono<PageResult<BootcampList>> listarBootcamps(BootcampCriteria criteria) {
        String sortBy = Optional.ofNullable(criteria.getSortBy())
                .orElse(Constants.SORT_BY_NOMBRE);

        switch (sortBy.toLowerCase()) {
            case Constants.SORT_BY_NOMBRE:
                return sortByName(criteria);
            case Constants.SORT_BY_CAPACIDAD_COUNT:
                return sortByCapacityCount(criteria);
            default:
                return Mono.error(new BusinessException(TechnicalMessage.INVALID_PARAMETERS));
        }
    }

    @Override
    public Mono<Void> eliminarBootcamp(Long bootcampId) {
        Logger log = LoggerFactory.getLogger(BootcampUseCase.class);

        return bootcampPersistencePort.findById(bootcampId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_NOT_FOUND)))
                .flatMap(bootcamp -> {
                    Mono<List<Long>> capacidadesDelBootcampIdsMono = capacidadClientPort.findCapacidadesByBootcampId(bootcampId)
                            .map(CapacidadSummary::getId)
                            .collectList()
                            .cache();

                    return capacidadesDelBootcampIdsMono.flatMap(capacidadesDelBootcampIds -> {
                        if (capacidadesDelBootcampIds.isEmpty()) {
                            return bootcampPersistencePort.deleteById(bootcampId);
                        }

                        return capacidadClientPort.eliminarCapacidadesPorBootcamp(bootcampId)
                                .then(capacidadesDelBootcampIdsMono)
                                .flatMap(originalIds -> {
                                    Mono<List<Long>> capacidadesHuerfanasIdsMono = Flux.fromIterable(originalIds)
                                            .filterWhen(capacidadId ->
                                                    capacidadClientPort.countBootcampsByCapacidadId(capacidadId)
                                                            .map(count -> count == 0)
                                            )
                                            .collectList()
                                            .doOnNext(ids -> log.info(">> DEBUG: Capacidades huérfanas encontradas: {}", ids))
                                            .cache();

                                    return capacidadesHuerfanasIdsMono.flatMap(capacidadesHuerfanasIds -> {
                                        if (capacidadesHuerfanasIds.isEmpty()) {
                                            log.info(">> DEBUG: No hay capacidades huérfanas. Borrando solo el bootcamp.");
                                            return bootcampPersistencePort.deleteById(bootcampId);
                                        }

                                        // ========== CAMBIO FUNDAMENTAL AQUÍ ==========

                                        // 1. PRIMERO, obtenemos la lista de tecnologías que VAMOS a afectar.
                                        return tecnologiaClientPort.findTecnologiaIdsByCapacidades(capacidadesHuerfanasIds)
                                                .collectList()
                                                .doOnNext(ids -> log.info(">> DEBUG: Tecnologías afectadas (antes de borrar): {}", ids))
                                                .flatMap(tecnologiasAfectadasIds -> {
                                                    // 2. AHORA definimos las operaciones de borrado.
                                                    Mono<Void> eliminarRelacionesTecnologia = tecnologiaClientPort.eliminarTecnologiasPorCapacidades(capacidadesHuerfanasIds)
                                                            .doOnSuccess(v -> log.info(">> DEBUG: Petición para eliminar relaciones capacidad-tecnología completada."));
                                                    Mono<Void> eliminarCapacidadesHuerfanas = capacidadClientPort.eliminarCapacidadesPorIds(capacidadesHuerfanasIds)
                                                            .doOnSuccess(v -> log.info(">> DEBUG: Petición para eliminar capacidades huérfanas completada."));

                                                    // 3. Ejecutamos los borrados.
                                                    return Mono.when(eliminarRelacionesTecnologia, eliminarCapacidadesHuerfanas)
                                                            .then(
                                                                    // 4. Usamos la lista 'tecnologiasAfectadasIds' que capturamos ANTES de borrar.
                                                                    Flux.fromIterable(tecnologiasAfectadasIds)
                                                                            .concatMap(tecnologiaId ->
                                                                                    tecnologiaClientPort.countCapacidadesByTecnologiaId(tecnologiaId)
                                                                                            .doOnNext(count -> log.info(">> DEBUG: Verificando tecnología ID {}. Conteo de capacidades asociadas: {}", tecnologiaId, count))
                                                                                            .filter(count -> count == 0)
                                                                                            .flatMap(count -> {
                                                                                                log.info(">> DEBUG: La tecnología ID {} será eliminada.", tecnologiaId);
                                                                                                return tecnologiaClientPort.eliminarTecnologiaPorId(tecnologiaId);
                                                                                            })
                                                                            )
                                                                            .then()
                                                            )
                                                            .then(bootcampPersistencePort.deleteById(bootcampId));
                                                });
                                    });
                                });
                    });
                })
                .then();
    }

    /**
     * Maneja rollback de manera compensatoria si algo falla.
     */
    private Mono<Void> rollbackEliminarBootcamp(List<Long> capacidadIds, Long bootcampId, Throwable error) {
        return Mono.defer(() -> {
            log.error("Error durante eliminación del bootcamp {}: {}", bootcampId, error.getMessage());
            return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_DELETE_FAILED));
        });
    }

    private Mono<PageResult<BootcampList>> sortByName(BootcampCriteria criteria) {
        return bootcampPersistencePort.findAll(criteria)
                .flatMap(page -> {
                    Flux<BootcampList> enrichedFlux = Flux.fromIterable(page.getContent())
                            .concatMap(bootcamp ->
                                    capacidadClientPort.findCapacidadesByBootcampId(bootcamp.id())
                                            .collectList()
                                            .map(capacidades ->
                                                    new BootcampList(
                                                            bootcamp.id(),
                                                            bootcamp.nombre(),
                                                            bootcamp.descripcion(),
                                                            bootcamp.fechaLanzamiento(),
                                                            bootcamp.duracion(),
                                                            capacidades
                                                    )
                                            )
                            );

                    return enrichedFlux.collectList()
                            .map(enrichedList -> {
                                enrichedList.sort((b1, b2) ->
                                        Constants.SORT_ORDER_DESC.equalsIgnoreCase(criteria.getSortOrder())
                                                ? b2.nombre().compareToIgnoreCase(b1.nombre())
                                                : b1.nombre().compareToIgnoreCase(b2.nombre())
                                );

                                return new PageResult<>(
                                        enrichedList,
                                        page.getTotalElements(),
                                        page.getTotalPages(),
                                        page.getCurrentPage(),
                                        page.getPageSize(),
                                        page.isFirst(),
                                        page.isLast()
                                );
                            });
                });
    }

    private Mono<PageResult<BootcampList>> sortByCapacityCount(BootcampCriteria criteria) {
        return bootcampPersistencePort.findAll(criteria)
                .flatMapMany(pageResult -> Flux.fromIterable(pageResult.getContent()))
                .concatMap(bootcamp ->
                        capacidadClientPort.findCapacidadesByBootcampId(bootcamp.id())
                                .collectList()
                                .map(capacidades ->
                                        new BootcampList(
                                                bootcamp.id(),
                                                bootcamp.nombre(),
                                                bootcamp.descripcion(),
                                                bootcamp.fechaLanzamiento(),
                                                bootcamp.duracion(),
                                                capacidades
                                        )
                                )
                )
                .collectList()
                .flatMap(fullList -> {
                    Comparator<BootcampList> comparator = Comparator.comparingInt(b -> b.capacidades().size());
                    if (Constants.SORT_ORDER_DESC.equalsIgnoreCase(criteria.getSortOrder())) {
                        comparator = comparator.reversed();
                    }
                    fullList.sort(comparator);

                    int totalElements = fullList.size();
                    int totalPages = (int) Math.ceil((double) totalElements / criteria.getSize());
                    int page = criteria.getPage();
                    int size = criteria.getSize();

                    int fromIndex = page * size;
                    if (fromIndex >= totalElements) {
                        return Mono.just(new PageResult<>(
                                List.of(),
                                (long) totalElements,
                                totalPages,
                                page,
                                size,
                                true,
                                true
                        ));
                    }

                    int toIndex = Math.min(fromIndex + size, totalElements);
                    List<BootcampList> paginatedList = fullList.subList(fromIndex, toIndex);

                    return Mono.just(new PageResult<>(
                            paginatedList,
                            (long) totalElements,
                            totalPages,
                            page,
                            size,
                            page == 0,
                            toIndex >= totalElements
                    ));
                });
    }
}
