package com.onclass.bootcamp.domain.constants;

public class Constants {
    private Constants() {}

    // ======== Límites de texto ========
    public static final int MAX_NOMBRE_BOOTCAMP = 50;
    public static final int MAX_DESCRIPCION_BOOTCAMP = 150;

    // ======== Reglas de capacidades asociadas ========
    public static final int MIN_CAPACIDADES = 1;
    public static final int MAX_CAPACIDADES = 4;

    // ======== Ordenamiento de listas ========
    public static final String SORT_BY_NOMBRE = "nombre";
    public static final String SORT_BY_CAPACIDAD_COUNT = "cantidadcapacidades";
    public static final String SORT_ORDER_ASC = "asc";
    public static final String SORT_ORDER_DESC = "desc";
}
