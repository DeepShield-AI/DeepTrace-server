package com.qcl.service;

import com.qcl.entity.AgentLog;
import org.springframework.data.domain.Page;

public interface EsAgentLogService {
    Page<AgentLog> search(String keyword, Integer pageNum, Integer pageSize);
}