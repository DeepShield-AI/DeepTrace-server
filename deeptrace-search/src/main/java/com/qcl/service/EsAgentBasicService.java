package com.qcl.service;

import com.qcl.entity.AgentBasic;
import org.springframework.data.domain.Page;

//import java.util.List;

public interface EsAgentBasicService {
    Page<AgentBasic> search(String keyword, Integer pageNum, Integer pageSize);
}