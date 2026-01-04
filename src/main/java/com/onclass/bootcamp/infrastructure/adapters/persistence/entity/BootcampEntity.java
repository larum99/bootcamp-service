package com.onclass.bootcamp.infrastructure.adapters.persistence.entity;

import com.onclass.bootcamp.infrastructure.adapters.util.EntityConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name = EntityConstants.BOOTCAMP_TABLE)
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class BootcampEntity {

    @Id
    private Long id;
    private String nombre;
    private String descripcion;
    @Column(EntityConstants.FECHA_LANZAMIENTO_COLUMN)
    private LocalDate fechaLanzamiento;
    private Integer duracion;
}
