package com.qcl.service;

import com.qcl.entity.AgentBasic;
import com.qcl.entity.User;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface EsMetricService {
    // 从 ES 查询 agent_basic 数据
    Page<AgentBasic> getAgentList(User user, String keyword, Integer pageNum, Integer pageSize);
    
    // 从 ES 查询 metric tags 数据
    Map<String, Object> getMetricTags();
    
    // 从 ES 查询曲线图指标数据
    Map<String, Object> getMetricChartData(com.qcl.entity.User user, String agentName, String namespace, String startTime, String endTime, String cpu, String device, String networkInterface, Integer dataSize, String name);
}
