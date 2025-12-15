package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TraceParam {
    private String ebpf =  "trace";
    private String sender =  "trace";
    @Valid
    private TraceSpanParam span;

    // Constructors
    public TraceParam() {
    }
}