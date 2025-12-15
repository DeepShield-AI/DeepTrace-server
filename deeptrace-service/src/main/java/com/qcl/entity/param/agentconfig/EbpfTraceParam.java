package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class EbpfTraceParam {
    /**
     * 日志等级 # 0 = off, 1 = debug, 3 = verbose, 4 = stats
     */
    @JsonProperty("log_level")
    private Integer logLevel;
    /**
     * 监控进程id
     */
    private Integer[] pids =  new Integer[]{523094};
    
    @Min(value = 128, message = "maxSockets must be between 128 and 512")
    @Max(value = 512, message = "maxSockets must be between 128 and 512")
    @JsonProperty("max_buffered_events")
    private Integer maxBufferedEvents;

    /**
     * 允许的probe，字符串列表
     */
    @JsonProperty("enabled_probes")
    private String[] enabledProbes;

    // Constructors
    public EbpfTraceParam() {}

    // Getters and Setters
    public Integer getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Integer logLevel) {
        this.logLevel = logLevel;
    }

    public Integer[] getPids() {
        return pids;
    }

    public void setPids(Integer[] pids) {
        this.pids = pids;
    }

    public Integer getMaxBufferedEvents() {
        return maxBufferedEvents;
    }

    public void setMaxBufferedEvents(Integer maxBufferedEvents) {
        this.maxBufferedEvents = maxBufferedEvents;
    }

    public String[] getEnabledProbes() {
        return enabledProbes;
    }

    public void setEnabledProbes(String[] enabledProbes) {
        this.enabledProbes = enabledProbes;
    }
}
