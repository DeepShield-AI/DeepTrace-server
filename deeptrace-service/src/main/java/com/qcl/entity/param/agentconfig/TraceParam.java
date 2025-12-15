package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TraceParam {
    private String ebpf =  "trace";
    private String sender =  "trace";
    private TraceSpanParam span;

    // Constructors
    public TraceParam() {
    }
}