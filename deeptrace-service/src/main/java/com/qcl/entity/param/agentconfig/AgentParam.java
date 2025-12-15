package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;

/**
 * 配置下发入参
 */
@Data
public class AgentParam {
    @Valid
    @JsonProperty("agent_info")
    private AgentInfoParam agentInfo;
    @Valid
    private MetricParam metric;
    @Valid
    private SenderParam sender;
    @Valid
    private TraceParam trace;
    @Valid
    private EbpfParam ebpf;
}
