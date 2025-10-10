package com.onclass.bootcamp.application.configSwagger;

public class ApiConstants {

    private ApiConstants() {}

    // Paths
    public static final String PATH_BOOTCAMP = "/bootcamps";

    // Headers
    public static final String HEADER_X_MESSAGE_ID = "x-message-id";
    public static final String HEADER_X_MESSAGE_ID_DESC = "Identificador único de la transacción para trazabilidad.";

    // Operaciones
    public static final String CREATE_BOOTCAMP_OPERATION_ID = "createBootcamp";
    public static final String CREATE_BOOTCAMP_SUMMARY = "Crear un nuevo bootcamp";

    // Request body
    public static final String REQUEST_BODY_DESCRIPTION = "Datos necesarios para registrar un bootcamp.";

    // Responses
    public static final String RESPONSE_201 = "Bootcamp creado exitosamente.";
    public static final String RESPONSE_400 = "Solicitud inválida. Verifique los datos enviados.";
    public static final String RESPONSE_500 = "Error interno del servidor.";

    // HTTP Codes
    public static final String HTTP_CREATED = "201";
    public static final String HTTP_BAD_REQUEST = "400";
    public static final String HTTP_INTERNAL_ERROR = "500";
}
