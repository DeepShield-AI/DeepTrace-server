package com.qcl.vo;

import java.util.List;

/**
 * 通用分页结果对象
 * @param <T> 分页内容类型
 */
public class PageResult<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    // 所有筛选项
    private List<String> allEndpoints;
    private List<String> allProtocols;
    private List<String> allStatusOptions;

    public PageResult(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public PageResult(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages =  (int) Math.ceil((double) totalElements / pageSize);
    }
    public PageResult(List<T> content, int pageSize, long totalElements) {
        this.content = content;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages =  (int) Math.ceil((double) totalElements / pageSize);
    }

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    // 所有筛选项的 getter和setter
    public List<String> getAllEndpoints() { return allEndpoints; }
    public void setAllEndpoints(List<String> allEndpoints) { this.allEndpoints = allEndpoints; }
    public List<String> getAllProtocols() { return allProtocols; }
    public void setAllProtocols(List<String> allProtocols) { this.allProtocols = allProtocols; }
    public List<String> getAllStatusOptions() { return allStatusOptions; }
    public void setAllStatusOptions(List<String> allStatusOptions) { this.allStatusOptions = allStatusOptions; }
}