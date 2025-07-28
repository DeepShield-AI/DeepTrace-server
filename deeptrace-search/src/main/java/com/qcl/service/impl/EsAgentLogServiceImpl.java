package com.qcl.service.impl;

import com.qcl.entity.AgentLog;
import com.qcl.repository.EsAgentLogRepository;
import com.qcl.service.EsAgentLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EsAgentLogServiceImpl implements EsAgentLogService {

    @Autowired
    private EsAgentLogRepository esAgentLogRepository;

    @Override
    public List<AgentLog> search(String keyword, Integer pageNum, Integer pageSize) {
        Iterable<AgentLog > iterable = esAgentLogRepository.findAll();
        List<AgentLog> result = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
        return result;
    }
}
