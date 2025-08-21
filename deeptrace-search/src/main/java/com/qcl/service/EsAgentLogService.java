package com.qcl.service;

import com.qcl.entity.AgentLog;
import org.springframework.data.domain.Page;

public interface EsAgentLogService {
    // 分页
    Page<AgentLog> search(String keyword, Integer pageNum, Integer pageSize);
    // 单条
    AgentLog findByLcuuid(String lcuuid);
}