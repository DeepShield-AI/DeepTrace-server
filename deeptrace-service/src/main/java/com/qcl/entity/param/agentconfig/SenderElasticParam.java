package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SenderElasticParam {
    @JsonProperty("trace")
    private SenderElasticTraceParam trace;

    // Constructors
    public SenderElasticParam() {}

    // Getters and Setters
    public SenderElasticTraceParam getTrace() {
        return trace;
    }

    public void setTrace(SenderElasticTraceParam trace) {
        this.trace = trace;
    }
}
