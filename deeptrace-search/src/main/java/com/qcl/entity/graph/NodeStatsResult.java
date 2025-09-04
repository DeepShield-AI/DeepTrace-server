package com.qcl.entity.graph;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NodeStatsResult {
    private String nodeId;
    private String containerName;
    private Double avgDuration;
    private Long errorCount;
    private Double totalCount;
    private Double errorRate;
    private Double qps;

    public NodeStatsResult(String nodeId, String containerName, Double avgDuration,
                           Long errorCount, Double totalCount, Double errorRate, Double qps) {
        this.nodeId = nodeId;
        this.containerName = containerName;
        this.avgDuration = avgDuration;
        this.errorCount = errorCount;
        this.totalCount = totalCount;
        this.errorRate = errorRate;
        this.qps = qps;
    }

}
