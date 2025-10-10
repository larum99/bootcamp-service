package com.onclass.bootcamp.infrastructure.adapters.persistence;

import com.onclass.bootcamp.domain.model.Bootcamp;
import com.onclass.bootcamp.domain.spi.BootcampPersistencePort;
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
}
