package com.onclass.bootcamp.infrastructure.entrypoints.dto;

import java.util.List;

public record CapacidadSummaryDTO(
        Long id,
        String nombre,
        List<TecnologiaSummaryDTO> tecnologias
) {}
