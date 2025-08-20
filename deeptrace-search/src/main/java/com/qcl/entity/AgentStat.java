package com.qcl.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;
import java.util.List;

/**
 * 监控信息实体类
 */
@Data
@EqualsAndHashCode
@Document(indexName = "agent_stats")
public class AgentStat implements Serializable {
    private static final long serialVersionUID = -1L;

    // 名称（主键）
    @Id
    @Field(name = "agent_name")
    private String agentName;

    // 进程ID
    private String pid;

    // 时间戳列表
    private List<String> timestamps;

    // CPU使用率
    @Field(name = "cpu_usages")
    private List<Float> cpuUsages;

    // 内存使用率
    @Field(name = "memory_usages")
    private List<Float> memoryUsages;

    // span数量
    @Field(name = "span_nums")
    private List<Long> spanNums;
}