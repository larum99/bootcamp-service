package com.onclass.bootcamp.domain.utils;

import java.util.List;

public class PageResult<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean isFirst;
    private boolean isLast;

    public PageResult(List<T> content, long totalElements, int totalPages, int currentPage, int pageSize, boolean isFirst, boolean isLast) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.isFirst = isFirst;
        this.isLast = isLast;
    }

    public List<T> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public boolean isLast() {
        return isLast;
    }

}
