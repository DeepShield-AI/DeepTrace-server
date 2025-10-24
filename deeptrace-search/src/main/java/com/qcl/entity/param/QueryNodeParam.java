package com.qcl.entity.param;

import lombok.Data;

/**
 * 资源分析
 */
@Data
public class QueryNodeParam extends QueryTracesParam{
    /**
     * 节点ID
     */
    private Long nodeId;
}
