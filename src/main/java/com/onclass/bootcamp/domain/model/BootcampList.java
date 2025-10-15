package com.onclass.bootcamp.domain.model;

import com.onclass.bootcamp.domain.utils.CapacidadSummary;

import java.time.LocalDate;
import java.util.List;

public record BootcampList (
    Long id,
    String nombre,
    String descripcion,
    LocalDate fechaLanzamiento,
    Integer duracion,
    List<CapacidadSummary> capacidades
) {}
