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

    /** 应用协议类型列表 */
    private List<String> protocol;

    /** 响应状态码列表 */
    private List<String> status;

    /** 最晚开始时间（秒级时间戳） */
    private Long startTime;

    /** 容器名称 */
    private String containerName;

    /** 最低端到端响应时间（毫秒） */
    private Long minE2eDuration;

    /** 最高端到端响应时间（毫秒） */
    private Long maxE2eDuration;

    /** 当前页码（从0开始） */
    private Integer pageNum;

    /** 每页条数 */
    private Integer pageSize;

    /** 排序字段（如start_time、e2e_duration等） */
    private String sortBy;

    /** 排序方式（desc或asc） */
    private String sortOrder;
}