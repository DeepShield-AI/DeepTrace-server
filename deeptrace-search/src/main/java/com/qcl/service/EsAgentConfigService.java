package com.qcl.service;

import com.qcl.entity.AgentConfig;

import java.util.List;

public interface EsAgentConfigService {
    List<AgentConfig> search(String keyword, Integer pageNum, Integer pageSize);
}
