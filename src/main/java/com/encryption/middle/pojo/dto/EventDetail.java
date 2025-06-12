package com.encryption.middle.pojo.dto;

import java.time.LocalDateTime;

public class EventDetail {
    private String id; //事件id

    private String stage; //阶段 stage1/stage2

    private String eventType; //事件类型 包特征识别

    private String method; //识别方法 包检测

    private Integer level; // 级别 0,1,2,3

    private LocalDateTime time; //时间

    private String duration; //持续时间

    private String detail; //详情

}
