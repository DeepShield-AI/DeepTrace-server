package com.qcl.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode
@Document(indexName = "agent_stats")
public class AgentStat implements Serializable {
    private static final long serialVersionUID = -1L;

    @Id
    @Field(name = "agent_name")
    private String agentName;
    @Field(name = "cpu_usages")
    private List<Float> cpuUsages;
    @Field(name = "memory_usages")
    private List<Float> memoryUsages;

    private String pid;
    @Field(name = "span_nums")
    private List<Long> spanNums;

    private List<String> timestamps;
}
