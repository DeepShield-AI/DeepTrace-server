package com.qcl.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 通用分页结果对象
 * @param <T> 分页内容类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public static <T> PageResult<T> of(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages) {
        return new PageResult<>(content, pageNumber, pageSize, totalElements, totalPages);
    }
}