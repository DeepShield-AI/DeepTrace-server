package com.qcl.service;

import com.qcl.entity.AgentConfig;
import java.util.List;

public interface EsAgentConfigService {
    // 分页
    List<AgentConfig> search(String keyword, Integer pageNum, Integer pageSize);
    // 单条
    AgentConfig findByAgentLcuuid(String agentLcuuid);
}