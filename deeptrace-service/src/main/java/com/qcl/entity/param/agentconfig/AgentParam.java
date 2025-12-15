package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 配置下发入参
 */
@Data
public class AgentParam {
    @JsonProperty("agent_info")
    private AgentInfoParam agentInfo;
    private MetricParam metric;
    private SenderParam sender;
    private TraceParam trace;
    private EbpfParam ebpf;
}
