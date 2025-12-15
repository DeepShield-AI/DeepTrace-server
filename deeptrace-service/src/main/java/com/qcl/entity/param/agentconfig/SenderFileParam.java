package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SenderFileParam {
    @JsonProperty("metric")
    private SenderFileMetricParam metric;

    // Constructors
    public SenderFileParam() {}

    // Getters and Setters
    public SenderFileMetricParam getMetric() {
        return metric;
    }

    public void setMetric(SenderFileMetricParam metric) {
        this.metric = metric;
    }
}
