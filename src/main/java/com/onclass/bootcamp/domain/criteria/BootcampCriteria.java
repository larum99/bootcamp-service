package com.onclass.bootcamp.domain.criteria;

public class BootcampCriteria {

    private int page;
    private int size;
    private String sortBy;
    private String sortOrder;
    private String nombre;

    public BootcampCriteria() {}

    public BootcampCriteria(int page, int size, String sortBy, String sortOrder, String nombre) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
        this.nombre = nombre;
    }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
