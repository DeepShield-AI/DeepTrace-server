package com.qcl.service;

import com.qcl.entity.AgentLog;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EsAgentLogService {

    List<AgentLog> search(String keyword, Integer pageNum, Integer pageSize);
}
