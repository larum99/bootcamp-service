package com.onclass.bootcamp.infrastructure.adapters.util;

public final class MapperConstants {
    
    // MapStruct component model
    public static final String SPRING_COMPONENT_MODEL = "spring";
    
    // Field names for mapping
    public static final String ID_FIELD = "id";
    public static final String NOMBRE_FIELD = "nombre";
    public static final String DESCRIPCION_FIELD = "descripcion";
    public static final String FECHA_LANZAMIENTO_FIELD = "fechaLanzamiento";
    public static final String DURACION_FIELD = "duracion";
    public static final String CAPACIDADES_FIELD = "capacidades";
    
    private MapperConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}