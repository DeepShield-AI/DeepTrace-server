package com.qcl.entity.param.agentconfig;

import lombok.Data;

/**
 * ebpf配置
 */
@Data
public class EbpfParam {
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
