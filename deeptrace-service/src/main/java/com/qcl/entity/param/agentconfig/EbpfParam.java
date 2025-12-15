package com.qcl.entity.param.agentconfig;

import jakarta.validation.Valid;
import lombok.Data;

/**
 * ebpf配置
 */
@Data
public class EbpfParam {
    @Valid
    private EbpfTraceParam trace;

    // Constructors
    public EbpfParam() {}

    // Getters and Setters
    public EbpfTraceParam getTrace() {
        return trace;
    }

    public void setTrace(EbpfTraceParam trace) {
        this.trace = trace;
    }
}
