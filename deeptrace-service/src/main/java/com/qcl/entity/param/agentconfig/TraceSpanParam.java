package com.qcl.entity.param.agentconfig;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class TraceSpanParam {
    @Min(value = 10, message = "cleanupInterval must be between 10 and 60")
    @Max(value = 60, message = "cleanupInterval must be between 10 and 60")
    @JsonProperty("cleanup_interval")
    private Integer cleanupInterval;

    @Min(value = 510, message = "maxSockets must be between 510 and 2048")
    @Max(value = 2048, message = "maxSockets must be between 510 and 2048")
    @JsonProperty("max_sockets")
    private Integer maxSockets;

    // Constructors
    public TraceSpanParam() {}

    // Getters and Setters
    public Integer getCleanupInterval() {
        return cleanupInterval;
    }

    public void setCleanupInterval(Integer cleanupInterval) {
        this.cleanupInterval = cleanupInterval;
    }

    public Integer getMaxSockets() {
        return maxSockets;
    }

    public void setMaxSockets(Integer maxSockets) {
        this.maxSockets = maxSockets;
    }
}