package com.qcl.entity.param;

import lombok.Data;

import java.util.List;

/**
 * Trace 分页与筛选参数（以最新文档为准）
 */
@Data
public class QueryTracesParam {
    /** Trace 唯一ID */
    private String traceId;

    /** 应用协议（支持多选） */
    private List<String> protocols;

    /** 应用端点（支持多选） */
    private List<String> endpoints;

    /** 响应状态（状态码，支持多选） */
    private List<String> statusCodes;

    /** 最晚的开始时间，秒级时间戳（start_time >= 此值） */
    private Long startTime;

    /** 容器名称 */
    private String containerName;

    /** 最低响应时间（e2e_duration >=） */
    private Long minE2eDuration;

    /** 最高响应时间（e2e_duration <=） */
    private Long maxE2eDuration;

    /** 当前页码（从0开始） */
    private Integer pageNo;

    /** 每页条数 */
    private Integer pageSize;

    /** 排序字段（如 start_time、e2e_duration） */
    private String sortBy;

    /** 排序方式：desc 或 asc */
    private String sortOrder;
}