package com.qcl.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Setter
@Getter
public class DistributeTableQueryDTO implements Serializable {
    private String traceId;
    private String endpoint;
    private String componentName;
    private String statusCode;
    private String protocol;
    private String serverIp;
    private String clientIp;
    private Integer minDuration;
    private Integer maxDuration;
    private int page;
    private int pageSize;
}
