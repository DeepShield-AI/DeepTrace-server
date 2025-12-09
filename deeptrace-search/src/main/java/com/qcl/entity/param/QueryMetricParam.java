package com.qcl.entity.param;

import co.elastic.clients.elasticsearch._types.FieldValue;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


/**
 * 路径分析查询参数
 */
@Data
@Getter
@Setter
public class QueryMetricParam {
    private String metricType;
    private Long startTime;
    private Long endTime;
}
