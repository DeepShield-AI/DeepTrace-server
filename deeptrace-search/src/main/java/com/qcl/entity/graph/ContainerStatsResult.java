package com.qcl.entity.graph;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContainerStatsResult {
    private String tgid;
    private String containerName;
    private Double avgDuration;
    private Long errorCount;
    private Double totalCount;
    private Double errorRate;
    private Double qps;

    public ContainerStatsResult(String tgid, String containerName, Double avgDuration,
                                Long errorCount, Double totalCount, Double errorRate, Double qps) {
        this.tgid = tgid;
        this.containerName = containerName;
        this.avgDuration = avgDuration;
        this.errorCount = errorCount;
        this.totalCount = totalCount;
        this.errorRate = errorRate;
        this.qps = qps;
    }

}
