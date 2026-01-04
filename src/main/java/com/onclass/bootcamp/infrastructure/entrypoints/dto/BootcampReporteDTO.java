package com.onclass.bootcamp.infrastructure.entrypoints.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BootcampReporteDTO {
    private Long idBootcamp;
    private String nombre;
    private String descripcion;
    private LocalDate fechaLanzamiento;
    private Integer duracion;
    private Integer cantidadCapacidades;
    private Integer cantidadTecnologias;
    private Integer cantidadPersonasInscritas;
}
