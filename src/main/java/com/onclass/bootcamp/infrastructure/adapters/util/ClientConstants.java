package com.onclass.bootcamp.infrastructure.adapters.util;

public final class ClientConstants {
    
    public static final String REPORTE_BOOTCAMPS_ENDPOINT = "/reporte-bootcamps";
    public static final String SERVICES_REPORTE_URL_PROPERTY = "${services.reporte.url}";
    
    // Capacidad service endpoints
    public static final String CAPACIDAD_BOOTCAMPS_ENDPOINT = "/capacidad-bootcamps";
    public static final String CAPACIDAD_BOOTCAMPS_BY_ID_ENDPOINT = "/capacidad-bootcamps/{bootcampId}/capacidades";
    public static final String CAPACIDAD_BOOTCAMPS_DELETE_ENDPOINT = "/capacidad-bootcamps/{bootcampId}";
    public static final String CAPACIDADES_COUNT_BOOTCAMPS_ENDPOINT = "/capacidades/{capacidadId}/bootcamps/count";
    public static final String CAPACIDADES_DELETE_ENDPOINT = "/capacidades";
    public static final String CAPACIDADES_VALIDATE_ENDPOINT = "/capacidades/validate";
    public static final String SERVICES_CAPACIDAD_URL_PROPERTY = "${services.capacidad.url}";
    
    // Tecnologia service endpoints
    public static final String CAPACIDAD_TECNOLOGIAS_DELETE_BY_CAPACIDADES_ENDPOINT = "/capacidad-tecnologias/by-capacidades";
    public static final String CAPACIDAD_TECNOLOGIAS_BY_CAPACIDADES_ENDPOINT = "/capacidad-tecnologias/tecnologias/by-capacidades";
    public static final String CAPACIDAD_TECNOLOGIAS_COUNT_BY_TECNOLOGIA_ENDPOINT = "/capacidad-tecnologias/count/by-tecnologia/{id}";
    public static final String TECNOLOGIAS_DELETE_BY_ID_ENDPOINT = "/tecnologias/{id}";
    public static final String SERVICES_TECNOLOGIA_URL_PROPERTY = "${services.tecnologia.url}";
    
    public static final String MESSAGE_ID_VALUE = "12345";
    
    private ClientConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}