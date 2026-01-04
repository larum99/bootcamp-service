package com.onclass.bootcamp.infrastructure.adapters.persistence.repository.impl;

import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampEntity;
import com.onclass.bootcamp.infrastructure.adapters.persistence.repository.CustomBootcampRepository;
import com.onclass.bootcamp.infrastructure.adapters.util.RepositoryConstants;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CustomBootcampRepositoryImpl implements CustomBootcampRepository {

    private final DatabaseClient databaseClient;

    public CustomBootcampRepositoryImpl(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Flux<BootcampEntity> findAllByFilters(BootcampCriteria criteria) {
        StringBuilder sql = new StringBuilder(RepositoryConstants.SELECT_BOOTCAMP_BASE_QUERY);

        if (RepositoryConstants.NOMBRE_SORT_FIELD.equalsIgnoreCase(criteria.getSortBy())) {
            sql.append(RepositoryConstants.ORDER_BY_NOMBRE)
                    .append(getValidSortOrder(criteria.getSortOrder()));
        }

        sql.append(RepositoryConstants.LIMIT_CLAUSE).append(criteria.getSize())
                .append(RepositoryConstants.OFFSET_CLAUSE).append(criteria.getPage() * criteria.getSize());

        return databaseClient.sql(sql.toString())
                .map((row, metadata) -> {
                    BootcampEntity entity = new BootcampEntity();
                    entity.setId(row.get(RepositoryConstants.ID_COLUMN, Long.class));
                    entity.setNombre(row.get(RepositoryConstants.NOMBRE_COLUMN, String.class));
                    entity.setDescripcion(row.get(RepositoryConstants.DESCRIPCION_COLUMN, String.class));
                    entity.setFechaLanzamiento(row.get(RepositoryConstants.FECHA_LANZAMIENTO_COLUMN, java.time.LocalDate.class));
                    entity.setDuracion(row.get(RepositoryConstants.DURACION_COLUMN, Integer.class));
                    return entity;
                })
                .all();
    }

    @Override
    public Mono<Long> countByFilters(BootcampCriteria criteria) {
        return databaseClient.sql(RepositoryConstants.COUNT_BOOTCAMP_QUERY)
                .map((row, metadata) -> row.get(RepositoryConstants.TOTAL_COLUMN, Long.class))
                .one();
    }

    private String getValidSortOrder(String sortOrder) {
        return (sortOrder != null && sortOrder.equalsIgnoreCase(RepositoryConstants.DESC_LOWERCASE)) ? RepositoryConstants.DESC_ORDER : RepositoryConstants.ASC_ORDER;
    }
}
