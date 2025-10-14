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
    public static final String RESPONSE_200 = "Listado de bootcamps devuelto correctamente.";
    public static final String RESPONSE_201 = "Bootcamp creado exitosamente.";
    public static final String RESPONSE_400 = "Solicitud inválida. Verifique los datos enviados.";
    public static final String RESPONSE_500 = "Error interno del servidor.";


    // HTTP Codes
    public static final String HTTP_CREATED = "201";
    public static final String HTTP_BAD_REQUEST = "400";
    public static final String HTTP_INTERNAL_ERROR = "500";
    public static final String HTTP_OK = "200";

    // Parámetros de consulta para GET paginado
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_PAGE_DESC = "Número de página (opcional, default 0)";
    public static final String PARAM_SIZE = "size";
    public static final String PARAM_SIZE_DESC = "Cantidad de elementos por página (opcional, default 10)";
    public static final String PARAM_SORT_BY = "sortBy";
    public static final String PARAM_SORT_BY_DESC = "Campo por el cual ordenar (opcional)";
    public static final String PARAM_SORT_ORDER = "sortOrder";
    public static final String PARAM_SORT_ORDER_DESC = "Dirección del orden: asc o desc (opcional)";

}
