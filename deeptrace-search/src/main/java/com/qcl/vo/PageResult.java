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

    // 新增四参数构造函数，自动计算总页数（无需手动传totalPages）
    public PageResult(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        // 避免pageSize为0时的除零异常，同时正确计算总页数
        this.totalPages = pageSize <= 0 ? 0 : (int) Math.ceil((double) totalElements / pageSize);
    }

    // 五参数静态工厂方法
    public static <T> PageResult<T> of(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages) {
        return new PageResult<>(content, pageNumber, pageSize, totalElements, totalPages);
    }

    // 四参数静态工厂方法，自动计算总页数
    public static <T> PageResult<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        return new PageResult<>(content, pageNumber, pageSize, totalElements);
    }
}