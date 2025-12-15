package com.qcl.entity.param.agentconfig;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 采集指标配置参数
 */
@Data
public class MetricParam {
    /**
     * 指标采集间隔，单位为秒
     */
    @Min(value = 1, message = "interval must be between 1 and 100")
    @Max(value = 100, message = "interval must be between 1 and 100")
    private Integer interval;
    /**
     * 默认值为"metric"
     */
    private String sender = "metric";

    // Constructors
    public MetricParam() {}

    // Getters and Setters
    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
