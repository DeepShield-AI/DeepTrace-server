package com.encryption.middle.pojo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DistributTableResponseDTO {
    private Double startTime;
    private Long client;
    private String server;
    private String serverPort;
    private String dataType;
    private String protocol;
    private Double delay;
    private Double traceId;
    private Integer spanCnt;
    private Integer statusCode;

}
