package com.onclass.bootcamp.domain.enums;

public enum TechnicalMessage {

    // ======== Errores genéricos ========
    INTERNAL_ERROR("500", "Ha ocurrido un error interno, por favor intente nuevamente", ""),
    INVALID_REQUEST("400", "Solicitud incorrecta, por favor verifique los datos", ""),
    INVALID_PARAMETERS("400-1", "Parámetros inválidos, por favor verifique los datos", ""),

    // ======== Mensajes específicos de Bootcamp ========
    BOOTCAMP_ALREADY_EXISTS("400-2", "El bootcamp ya está registrado", "nombre"),
    BOOTCAMP_NOMBRE_REQUIRED("400-3", "El nombre del bootcamp es obligatorio", "nombre"),
    BOOTCAMP_NOMBRE_TOO_LONG("400-4", "El nombre del bootcamp supera los 50 caracteres", "nombre"),
    BOOTCAMP_DESCRIPCION_REQUIRED("400-5", "La descripción del bootcamp es obligatoria", "descripcion"),
    BOOTCAMP_DESCRIPCION_TOO_LONG("400-6", "La descripción del bootcamp supera los 150 caracteres", "descripcion"),
    BOOTCAMP_FECHA_REQUIRED("400-7", "La fecha de lanzamiento del bootcamp es obligatoria", "fechaLanzamiento"),
    BOOTCAMP_FECHA_INVALID("400-8", "La fecha de lanzamiento no puede ser anterior a la actual", "fechaLanzamiento"),
    BOOTCAMP_DURACION_REQUIRED("400-9", "La duración del bootcamp debe ser mayor a 0", "duracion"),

    // ======== Validaciones de capacidades asociadas ========
    BOOTCAMP_MIN_CAPACIDADES("400-10", "El bootcamp debe tener al menos 1 capacidad asociada", "capacidades"),
    BOOTCAMP_MAX_CAPACIDADES("400-11", "El bootcamp no puede tener más de 4 capacidades", "capacidades"),
    BOOTCAMP_CAPACIDADES_DUPLICADAS("400-12", "El bootcamp no puede tener capacidades repetidas", "capacidades"),

    // ======== Mensaje de éxito ========
    BOOTCAMP_CREATED("201", "Bootcamp creado exitosamente", "");

    private final String code;
    private final String description;
    private final String param;

    TechnicalMessage(String code, String description, String param) {
        this.code = code;
        this.description = description;
        this.param = param;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getParam() {
        return param;
    }
}
