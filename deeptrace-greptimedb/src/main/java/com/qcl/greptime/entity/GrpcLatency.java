package com.qcl.greptime.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class GrpcLatency {
    private String host;
    private String methodName;
    private Double latency;
    // TIMESTAMP 对应 Long 类型（毫秒时间戳）
    private Timestamp ts;
}
