package com.onclass.bootcamp.application.configSwagger;

public class ApiExamples {

    private ApiExamples() {}

    public static final String BOOTCAMP_DTO_JSON = """
        {
              "nombre": "Bootcamp Java prueba 2",
              "descripcion": "Programa intensivo de backend con Spring Boot y Microservicios",
               "fechaLanzamiento": "2025-11-01",
               "duracion": 12,
               "capacidades": [1]
        }
    """;
    
    // Delete operation constants
    public static final String DELETE_BOOTCAMP_SUMMARY = "Eliminar un bootcamp";
    public static final String DELETE_BOOTCAMP_DESCRIPTION = "Elimina un bootcamp por su identificador. La operación también elimina sus capacidades y tecnologías asociadas si no están relacionadas con otros bootcamps.";
    public static final String DELETE_BOOTCAMP_OPERATION_ID = "deleteBootcamp";
    public static final String ID_PARAM_DESCRIPTION = "ID del bootcamp que se desea eliminar";
    public static final String DELETE_SUCCESS_DESCRIPTION = "Bootcamp eliminado exitosamente";
    public static final String NOT_FOUND_DESCRIPTION = "No se encontró el bootcamp con el ID especificado";
    
    // Method and parameter names
    public static final String DELETE_BOOTCAMP_METHOD = "deleteBootcamp";
    public static final String ID_PARAM_NAME = "id";
    public static final String PATH_ID_SEGMENT = "/{id}";
    
    // Create operation constants
    public static final String CREATE_BOOTCAMP_METHOD = "createBootcamp";
    public static final String EXAMPLE_BOOTCAMP_NAME = "Ejemplo Bootcamp";
    
    // Get operation constants
    public static final String GET_BOOTCAMPS_METHOD = "getBootcamps";
    public static final String GET_BOOTCAMPS_OPERATION_ID = "getBootcamps";
    public static final String GET_BOOTCAMPS_SUMMARY = "Obtener listado de bootcamps";
    public static final String GET_BOOTCAMPS_DESCRIPTION = "Obtiene todos los bootcamps paginados. Se pueden incluir parámetros opcionales de paginación y ordenamiento.";
}