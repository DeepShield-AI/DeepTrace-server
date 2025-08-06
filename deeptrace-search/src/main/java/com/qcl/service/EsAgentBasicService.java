package com.qcl.service;

import com.qcl.entity.AgentBasic;
import com.qcl.entity.AgentLog;
import org.springframework.data.domain.Page;

//import java.util.List;

public interface EsAgentBasicService {
    Page<AgentBasic> search(String keyword, Integer pageNum, Integer pageSize);
}