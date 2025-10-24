package com.qcl.entity.param;

import lombok.Data;


/**
 * 路径分析查询参数
 */
@Data
public class QueryEdgeParam extends QueryTracesParam{
    /**
     * 源节点
     */
    private Long srcNodeId;
    /**
     * 目标节点
     */
    private Long dstNodeId;
}
