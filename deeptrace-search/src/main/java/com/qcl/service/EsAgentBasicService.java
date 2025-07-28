package com.qcl.service;

import com.qcl.entity.AgentBasic;
import com.qcl.entity.AgentLog;

import java.util.List;

public interface EsAgentBasicService {
    List<AgentBasic> search(String keyword, Integer pageNum, Integer pageSize);
}
