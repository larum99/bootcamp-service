package com.onclass.bootcamp.application.config;

import com.onclass.bootcamp.domain.api.BootcampServicePort;
import com.onclass.bootcamp.domain.spi.BootcampPersistencePort;
import com.onclass.bootcamp.domain.spi.CapacidadClientPort;
import com.onclass.bootcamp.domain.usecase.BootcampUseCase;
import com.onclass.bootcamp.infrastructure.adapters.persistence.BootcampPersistenceAdapter;
import com.onclass.bootcamp.infrastructure.adapters.persistence.mapper.BootcampEntityMapper;
import com.onclass.bootcamp.infrastructure.adapters.persistence.repository.BootcampRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public BootcampPersistencePort bootcampPersistencePort(
            BootcampRepository bootcampRepository,
            BootcampEntityMapper bootcampEntityMapper
    ) {
        return new BootcampPersistenceAdapter(bootcampRepository, bootcampEntityMapper);
    }

    @Bean
    public BootcampServicePort bootcampServicePort(
            BootcampPersistencePort bootcampPersistencePort,
            CapacidadClientPort capacidadClientPort
    ) {
        return new BootcampUseCase(bootcampPersistencePort, capacidadClientPort);
    }
}
