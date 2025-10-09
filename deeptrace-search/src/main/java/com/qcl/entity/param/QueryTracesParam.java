package com.qcl.entity.param;

import lombok.Data;

import java.util.List;

@Data
public class QueryTracesParam {
    private String traceId;
    /**
     * 应用协议
     */
    private List<String> protocols;
    /*
     * 应用端点
     */
    private List<String> endpoints;
    /**
     * 响应状态
     */
    private List<String> statusCodes;

    /**
     * 最晚的开始时间，值为秒级时间戳  eg:1755926183
     */
    private Long startTime;
    /**
     * 最早的结束时间，值为秒级时间戳  eg:1755926183
     */
    private Long endTime;

    /**
     * 容器名称
     */
    private String containerName;
    /**
     * 最低响应时间
     */
    private Long minE2eDuration;
    /**
     * 最高响应时间
     */
    private Long maxE2eDuration;

    private Integer pageNo;
    private Integer pageSize;

    //排序字段
    private String sortBy;

    //desc asc
    private String sortOrder;
}
