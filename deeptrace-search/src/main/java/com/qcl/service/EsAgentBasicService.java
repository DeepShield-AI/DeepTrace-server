package com.qcl.service;

import com.qcl.entity.AgentBasic;
import com.qcl.result.PageResult;

public interface EsAgentBasicService {
    PageResult<AgentBasic> search(String keyword, Integer pageNum, Integer pageSize);
}