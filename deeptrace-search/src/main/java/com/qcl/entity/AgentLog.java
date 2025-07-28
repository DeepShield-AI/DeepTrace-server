package com.qcl.entity;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * 运行日志
 */
@Data
@EqualsAndHashCode
@Document(indexName = "agent_log")
public class AgentLog implements Serializable {
    private static final long serialVersionUID = -1L;
    @Field(name = "agent_name")
    private String agentName;

    @Id
    private String lcuuid;

    @Field(type = FieldType.Object)
    private Logs logs;
}
