package com.qcl.entity.param;

import lombok.Data;

import java.util.List;

@Data
public class QueryTracesParam {
    private String traceId;
    /**
     * 应用协议
     */
    private List<String> protocol;
    /**
     * 响应状态
     */
    private List<String> status;

    /**
     * 请求的开始时间，值为秒级时间戳
     */
    private Long startTime;
    private String containerName;
    private Long minE2eDuration;
    private Long maxE2eDuration;

    private Integer pageNo;
    private Integer pageSize;


    private String sortBy;

    //desc asc
    private String sortOrder;
}
