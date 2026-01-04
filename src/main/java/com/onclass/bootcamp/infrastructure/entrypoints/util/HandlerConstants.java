package com.onclass.bootcamp.infrastructure.entrypoints.util;

public final class HandlerConstants {
    
    // Query parameter names
    public static final String PAGE_PARAM = "page";
    public static final String SIZE_PARAM = "size";
    public static final String SORT_BY_PARAM = "sortBy";
    public static final String SORT_ORDER_PARAM = "sortOrder";
    
    // Default values
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;
    public static final String DEFAULT_SORT_BY = "nombre";
    public static final String DEFAULT_SORT_ORDER = "asc";
    
    // Path variable names
    public static final String ID_PATH_VARIABLE = "id";
    
    // Log messages
    public static final String BOOTCAMP_CREATED_LOG = "Bootcamp creado con messageId: {}";
    public static final String ERROR_PROCESSING_REQUEST_LOG = "Error procesando solicitud con messageId: {}";
    
    private HandlerConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}