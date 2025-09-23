package com.qcl.entity.graph;


import lombok.Data;

@Data
public class EdgeStatsResult {
    private String srcNodeId;
    private String dstNodeId;
    private Double avgDuration;
    private Double totalCount;
    private Double qps;

    public EdgeStatsResult(String srcNodeId, String dstNodeId, Double avgDuration, Double totalCount, Double qps) {
        this.srcNodeId = srcNodeId;
        this.dstNodeId = dstNodeId;
        this.avgDuration = avgDuration;
        this.totalCount = totalCount;
        this.qps = qps;
    }

    // Getters and setters
    public String getSrcNodeId() {
        return srcNodeId;
    }

    public void setSrcNodeId(String srcNodeId) {
        this.srcNodeId = srcNodeId;
    }

    public String getDstNodeId() {
        return dstNodeId;
    }

    public void setDstNodeId(String dstNodeId) {
        this.dstNodeId = dstNodeId;
    }

    public Double getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(Double avgDuration) {
        this.avgDuration = avgDuration;
    }

    public Double getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Double totalCount) {
        this.totalCount = totalCount;
    }

    public Double getQps() {
        return qps;
    }

    public void setQps(Double qps) {
        this.qps = qps;
    }
}
