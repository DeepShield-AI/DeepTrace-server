package com.qcl.service.impl;

import com.qcl.entity.AgentLog;
import com.qcl.repository.EsAgentLogRepository;
import com.qcl.service.EsAgentLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class EsAgentLogServiceImpl implements EsAgentLogService {
    @Autowired
    private EsAgentLogRepository esAgentLogRepository;

    @Override
    public Page<AgentLog> search(String keyword, Integer pageNum, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        if (keyword == null || keyword.isEmpty()) {
            return esAgentLogRepository.findAll(pageRequest);
        }
        return esAgentLogRepository.findByAgentNameContaining(keyword, pageRequest);
    }
}