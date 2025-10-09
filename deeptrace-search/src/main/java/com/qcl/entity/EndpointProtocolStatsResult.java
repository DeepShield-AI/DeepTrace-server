package com.qcl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 端点数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointProtocolStatsResult {
    private String endpoint;
    private String protocol;
    private Double totalCount;
    private String minTime;
    private String maxTime;
    private Double qps;
    private Double avgDuration;
    private Object p75Duration;
    private Object p99Duration;
    private double errorCount;
    private Double errorRate;
}
