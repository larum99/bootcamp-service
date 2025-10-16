package com.onclass.bootcamp.infrastructure.adapters.persistence;

import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.model.BootcampList;
import com.onclass.bootcamp.domain.spi.BootcampPersistencePort;
import com.onclass.bootcamp.domain.utils.PageResult;
import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampEntity;
import com.onclass.bootcamp.infrastructure.adapters.persistence.mapper.BootcampEntityMapper;
import com.onclass.bootcamp.infrastructure.adapters.persistence.repository.BootcampRepository;
import reactor.core.publisher.Mono;

public class BootcampPersistenceAdapter implements BootcampPersistencePort {

    private final BootcampRepository bootcampRepository;
    private final BootcampEntityMapper bootcampEntityMapper;

    public BootcampPersistenceAdapter(BootcampRepository bootcampRepository,
                                      BootcampEntityMapper bootcampEntityMapper) {
        this.bootcampRepository = bootcampRepository;
        this.bootcampEntityMapper = bootcampEntityMapper;
    }

    @Override
    public Mono<Bootcamp> saveBootcamp(Bootcamp bootcamp) {
        BootcampEntity entity = bootcampEntityMapper.toEntity(bootcamp);
        return bootcampRepository.save(entity)
                .map(bootcampEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existByNombre(String nombre) {
        return bootcampRepository.findByNombre(nombre)
                .map(bootcampEntityMapper::toModel)
                .map(b -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<PageResult<BootcampList>> findAll(BootcampCriteria criteria) {
        return bootcampRepository.findAllByFilters(criteria)
                .map(bootcampEntityMapper::toListModel)
                .collectList()
                .zipWith(bootcampRepository.countByFilters(criteria))
                .map(tuple -> {
                    long totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / criteria.getSize());
                    boolean isFirst = criteria.getPage() == 0;
                    boolean isLast = criteria.getPage() == totalPages - 1;

                    return new PageResult<>(
                            tuple.getT1(),
                            totalElements,
                            totalPages,
                            criteria.getPage(),
                            criteria.getSize(),
                            isFirst,
                            isLast
                    );
                });
    }

    @Override
    public Mono<Void> deleteById(Long bootcampId) {
        return bootcampRepository.deleteById(bootcampId);
    }

    @Override
    public Mono<Bootcamp> findById(Long bootcampId) {
        return bootcampRepository.findById(bootcampId)
                .map(bootcampEntityMapper::toModel);
    }
}
