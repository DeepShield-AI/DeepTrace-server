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
    private String containerName;
    private Long minE2eDuration;
    private Long maxE2eDuration;

    private Integer pageNo;
    private Integer pageSize;


    private String sortBy;

    //desc asc
    private String sortOrder;
}
