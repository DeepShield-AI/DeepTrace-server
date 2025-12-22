package com.qcl.service;

import com.qcl.entity.AgentBasic;
import org.springframework.data.domain.Page;

public interface EsAgentBasicService {
    // 分页查询
    Page<AgentBasic> search(String keyword, Integer pageNum, Integer pageSize);
    // 单条
    AgentBasic findByLcuuid(String lcuuid);
}