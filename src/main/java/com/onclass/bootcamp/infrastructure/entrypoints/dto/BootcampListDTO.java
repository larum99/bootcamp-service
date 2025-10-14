package com.onclass.bootcamp.infrastructure.entrypoints.dto;

import java.time.LocalDate;
import java.util.List;

public record BootcampListDTO(
        Long id,
        String nombre,
        String descripcion,
        LocalDate fechaLanzamiento,
        Integer duracion,
        List<CapacidadSummaryDTO> capacidades
) {}

