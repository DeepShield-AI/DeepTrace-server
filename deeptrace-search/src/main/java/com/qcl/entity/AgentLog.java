// deeptrace-search/src/main/java/com/qcl/entity/AgentLog.java
package com.qcl.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@Document(indexName = "agent_log")
public class AgentLog implements Serializable {
    private static final long serialVersionUID = -1L;

    @Id
    private String id; // ES的_id

    @Field(name = "agent_name")
    private String agentName;

    @Field(name = "lcuuid")
    private String lcuuid;

    @Field(name = "content")
    private String content;

    @Field(name = "level")
    private String level;

    @Field(name = "timestamp")
    private String timestamp;

//    @Field(name = "logs")
//    private Logs logs;
}