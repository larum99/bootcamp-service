package com.onclass.bootcamp.infrastructure.adapters.util;

public final class RepositoryConstants {
    
    // SQL queries
    public static final String SELECT_BOOTCAMP_BASE_QUERY = """
            SELECT b.id, b.nombre, b.descripcion, b.fecha_lanzamiento, b.duracion
            FROM bootcamp b
            """;
    public static final String COUNT_BOOTCAMP_QUERY = "SELECT COUNT(*) AS total FROM bootcamp b";
    
    // SQL clauses
    public static final String ORDER_BY_NOMBRE = " ORDER BY b.nombre ";
    public static final String LIMIT_CLAUSE = " LIMIT ";
    public static final String OFFSET_CLAUSE = " OFFSET ";
    
    // Column names
    public static final String ID_COLUMN = "id";
    public static final String NOMBRE_COLUMN = "nombre";
    public static final String DESCRIPCION_COLUMN = "descripcion";
    public static final String FECHA_LANZAMIENTO_COLUMN = "fecha_lanzamiento";
    public static final String DURACION_COLUMN = "duracion";
    public static final String TOTAL_COLUMN = "total";
    
    // Sort orders
    public static final String DESC_ORDER = "DESC";
    public static final String ASC_ORDER = "ASC";
    public static final String DESC_LOWERCASE = "desc";
    
    // Sort fields
    public static final String NOMBRE_SORT_FIELD = "nombre";
    
    private RepositoryConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}