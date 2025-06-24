package com.encryption.middle.pojo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistributTableResponseDTO {
    private String traceId;
    private Integer spanNum;
    private Integer e2eDuration;
    private String endpoint;
    private String componentName;
    private String serverIp;
    private Integer serverPort;
    private String clientIp;
    private Integer clientPort;
    private String protocol;
    private String statusCode;

//    private String dataType;
//    private Long startTime;
//    private Long endTime;
//    private Long srcIp;
//    private Long dstIp;
//    private Double duration;
//    private Integer srcPort;
//    private Integer dstPort;
//    private String direction;
//
//    private Long client;
//    private String server;
//    private Integer spanCnt;
}
