package com.onclass.bootcamp.infrastructure.adapters.persistence.repository.impl;

import com.onclass.bootcamp.domain.criteria.BootcampCriteria;
import com.onclass.bootcamp.infrastructure.adapters.persistence.entity.BootcampEntity;
import com.onclass.bootcamp.infrastructure.adapters.persistence.repository.CustomBootcampRepository;
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
        StringBuilder sql = new StringBuilder("""
                SELECT b.id, b.nombre, b.descripcion, b.fecha_lanzamiento, b.duracion
                FROM bootcamp b
                """);

        if ("nombre".equalsIgnoreCase(criteria.getSortBy())) {
            sql.append(" ORDER BY b.nombre ")
                    .append(getValidSortOrder(criteria.getSortOrder()));
        }

        sql.append(" LIMIT ").append(criteria.getSize())
                .append(" OFFSET ").append(criteria.getPage() * criteria.getSize());

        return databaseClient.sql(sql.toString())
                .map((row, metadata) -> {
                    BootcampEntity entity = new BootcampEntity();
                    entity.setId(row.get("id", Long.class));
                    entity.setNombre(row.get("nombre", String.class));
                    entity.setDescripcion(row.get("descripcion", String.class));
                    entity.setFechaLanzamiento(row.get("fecha_lanzamiento", java.time.LocalDate.class));
                    entity.setDuracion(row.get("duracion", Integer.class));
                    return entity;
                })
                .all();
    }

    @Override
    public Mono<Long> countByFilters(BootcampCriteria criteria) {
        String sql = "SELECT COUNT(*) AS total FROM bootcamp b";
        return databaseClient.sql(sql)
                .map((row, metadata) -> row.get("total", Long.class))
                .one();
    }

    private String getValidSortOrder(String sortOrder) {
        return (sortOrder != null && sortOrder.equalsIgnoreCase("desc")) ? "DESC" : "ASC";
    }
}
