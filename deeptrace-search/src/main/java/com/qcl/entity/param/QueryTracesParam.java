package com.qcl.entity.param;

import lombok.Data;

import java.util.List;

/**
 * Trace 分页与筛选参数
 */
@Data
public class QueryTracesParam {
    /** Trace唯一ID */
    private String traceId;
    /**
     * 应用协议
     */
    private List<String> protocols;
    /**
     * 应用端点
     */
    private List<String> endpoints;
    /** 响应状态码列表 */
    private List<String> statusCodes;
    /**
     * 最晚的开始时间，值为秒级时间戳：必填  eg:1755926183
     */
    private Long startTime;
    /**
     * 最早的结束时间，值为秒级时间戳 ：必填 eg:1755926183
     */
    private Long endTime;
    /**
     * 容器名称
     */
    private String containerName;
    /**
     * 最低端到端响应时间
     */
    private Long minE2eDuration;
    /**
     * 最高端到端响应时间
     */
    private Long maxE2eDuration;
    /** 当前页（从0开始） */
    private Integer pageNum;
    /** 每页条数 */
    private Integer pageSize;
    /** 排序字段（如start_time、e2e_duration等） */
    private String sortBy;
    /** 排序方式（desc或asc） */
    private String sortOrder;
}
