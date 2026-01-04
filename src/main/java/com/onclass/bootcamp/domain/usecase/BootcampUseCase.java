package com.onclass.bootcamp.domain.usecase;

import com.onclass.bootcamp.domain.api.BootcampServicePort;
import com.onclass.bootcamp.domain.constants.Constants;
import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.domain.enums.TechnicalMessage;
import com.onclass.bootcamp.domain.exceptions.BusinessException;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampList;
import com.onclass.bootcamp.domain.spi.BootcampPersistencePort;
import com.onclass.bootcamp.domain.spi.BootcampReporteClientPort;
import com.onclass.bootcamp.domain.spi.CapacidadClientPort;
import com.onclass.bootcamp.domain.spi.TecnologiaClientPort;
import com.onclass.bootcamp.domain.utils.CapacidadSummary;
import com.onclass.bootcamp.domain.utils.PageResult;
import com.onclass.bootcamp.infrastructure.entrypoints.dto.BootcampReporteDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;

public class BootcampUseCase implements BootcampServicePort {

    private final BootcampPersistencePort bootcampPersistencePort;
    private final CapacidadClientPort capacidadClientPort;
    private final TecnologiaClientPort tecnologiaClientPort;
    private final BootcampReporteClientPort bootcampReporteClientPort;


    public BootcampUseCase(BootcampPersistencePort bootcampPersistencePort,
                           CapacidadClientPort capacidadClientPort,
                           TecnologiaClientPort tecnologiaClientPort,
                           BootcampReporteClientPort bootcampReporteClientPort) {
        this.bootcampPersistencePort = bootcampPersistencePort;
        this.capacidadClientPort = capacidadClientPort;
        this.tecnologiaClientPort = tecnologiaClientPort;
        this.bootcampReporteClientPort = bootcampReporteClientPort;
    }


    @Override
    public Mono<Bootcamp> registrarBootcamp(Bootcamp bootcamp, String messageId) {
        return validarBootcamp(bootcamp)
                .then(Mono.defer(() -> 
                        bootcampPersistencePort.existByNombre(bootcamp.nombre())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_ALREADY_EXISTS));
                                    }
                                    return procesarRegistroBootcamp(bootcamp);
                                })));
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
        return bootcampPersistencePort.findById(bootcampId)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_NOT_FOUND)))
                .flatMap(bootcamp -> procesarEliminacionBootcamp(bootcampId));
    }

    @Override
    public Mono<BootcampList> obtenerBootcampPorId(Long id) {
        return bootcampPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.BOOTCAMP_NOT_FOUND)))
                .flatMap(bootcamp ->
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

    private Mono<Void> validarBootcamp(Bootcamp bootcamp) {
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
        return validarCapacidades(bootcamp.capacidades());
    }

    private Mono<Void> validarCapacidades(List<Long> capacidades) {
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
        return Mono.empty();
    }

    private Mono<Bootcamp> procesarRegistroBootcamp(Bootcamp bootcamp) {
        List<Long> capacidades = bootcamp.capacidades();
        return capacidadClientPort.validateCapacidadesExist(capacidades)
                .flatMap(allExist -> {
                    if (!allExist) {
                        return Mono.error(new BusinessException(TechnicalMessage.CAPACIDAD_NOT_FOUND));
                    }
                    return bootcampPersistencePort.saveBootcamp(bootcamp)
                            .flatMap(saved -> asociarCapacidadesYNotificar(saved, capacidades));
                });
    }

    private Mono<Bootcamp> asociarCapacidadesYNotificar(Bootcamp bootcamp, List<Long> capacidades) {
        return capacidadClientPort.associateBootcampWithCapacidades(bootcamp.id(), capacidades)
                .then(tecnologiaClientPort.findTecnologiaIdsByCapacidades(capacidades)
                        .collectList()
                        .flatMap(tecnologias -> {
                            BootcampReporteDTO reporteDTO = BootcampReporteDTO.builder()
                                    .idBootcamp(bootcamp.id())
                                    .nombre(bootcamp.nombre())
                                    .descripcion(bootcamp.descripcion())
                                    .fechaLanzamiento(bootcamp.fechaLanzamiento())
                                    .duracion(bootcamp.duracion())
                                    .cantidadCapacidades(capacidades.size())
                                    .cantidadTecnologias(tecnologias.size())
                                    .cantidadPersonasInscritas(0)
                                    .build();
                            return bootcampReporteClientPort.notificarNuevoBootcamp(reporteDTO)
                                    .thenReturn(bootcamp);
                        }));
    }

    private Mono<Void> procesarEliminacionBootcamp(Long bootcampId) {
        Mono<List<Long>> capacidadesDelBootcampIdsMono = capacidadClientPort.findCapacidadesByBootcampId(bootcampId)
                .map(CapacidadSummary::getId)
                .collectList()
                .cache();

        return capacidadesDelBootcampIdsMono.flatMap(capacidadesDelBootcampIds -> {
            if (capacidadesDelBootcampIds.isEmpty()) {
                return bootcampPersistencePort.deleteById(bootcampId);
            }
            return eliminarCapacidadesYDependencias(bootcampId, capacidadesDelBootcampIdsMono);
        });
    }

    private Mono<Void> eliminarCapacidadesYDependencias(Long bootcampId, Mono<List<Long>> capacidadesDelBootcampIdsMono) {
        return capacidadClientPort.eliminarCapacidadesPorBootcamp(bootcampId)
                .then(capacidadesDelBootcampIdsMono)
                .flatMap(originalIds -> procesarCapacidadesHuerfanas(bootcampId, originalIds));
    }

    private Mono<Void> procesarCapacidadesHuerfanas(Long bootcampId, List<Long> originalIds) {
        Mono<List<Long>> capacidadesHuerfanasIdsMono = Flux.fromIterable(originalIds)
                .filterWhen(capacidadId ->
                        capacidadClientPort.countBootcampsByCapacidadId(capacidadId)
                                .map(count -> count == 0))
                .collectList()
                .cache();

        return capacidadesHuerfanasIdsMono.flatMap(capacidadesHuerfanasIds -> {
            if (capacidadesHuerfanasIds.isEmpty()) {
                return bootcampPersistencePort.deleteById(bootcampId);
            }
            return eliminarTecnologiasYCapacidadesHuerfanas(bootcampId, capacidadesHuerfanasIds);
        });
    }

    private Mono<Void> eliminarTecnologiasYCapacidadesHuerfanas(Long bootcampId, List<Long> capacidadesHuerfanasIds) {
        return tecnologiaClientPort.findTecnologiaIdsByCapacidades(capacidadesHuerfanasIds)
                .collectList()
                .flatMap(tecnologiasAfectadasIds -> {
                    Mono<Void> eliminarRelacionesTecnologia = tecnologiaClientPort.eliminarTecnologiasPorCapacidades(capacidadesHuerfanasIds);
                    Mono<Void> eliminarCapacidadesHuerfanas = capacidadClientPort.eliminarCapacidadesPorIds(capacidadesHuerfanasIds);

                    return Mono.when(eliminarRelacionesTecnologia, eliminarCapacidadesHuerfanas)
                            .then(eliminarTecnologiasHuerfanas(tecnologiasAfectadasIds))
                            .then(bootcampPersistencePort.deleteById(bootcampId));
                });
    }

    private Mono<Void> eliminarTecnologiasHuerfanas(List<Long> tecnologiasAfectadasIds) {
        return Flux.fromIterable(tecnologiasAfectadasIds)
                .concatMap(tecnologiaId ->
                        tecnologiaClientPort.countCapacidadesByTecnologiaId(tecnologiaId)
                                .filter(count -> count == 0)
                                .flatMap(count -> tecnologiaClientPort.eliminarTecnologiaPorId(tecnologiaId)))
                .then();
    }
}
