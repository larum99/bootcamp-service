package com.onclass.bootcamp.infrastructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BootcampCapacidadDTO {
    private Long bootcampId;
    private Long capacidadId;
}
