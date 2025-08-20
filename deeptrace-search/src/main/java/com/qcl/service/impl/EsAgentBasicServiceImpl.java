package com.qcl.service.impl;

import com.qcl.entity.AgentBasic;
import com.qcl.entity.AgentLog;
import com.qcl.repository.EsAgentBasicRepository;
import com.qcl.repository.EsAgentLogRepository;
import com.qcl.service.EsAgentBasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EsAgentBasicServiceImpl implements EsAgentBasicService {
    @Autowired
    private EsAgentBasicRepository esAgentBasicRepository;

    @Override
    public List<AgentBasic> search(String keyword, Integer pageNum, Integer pageSize) {
        Iterable<AgentBasic > iterable = esAgentBasicRepository.findAll();
        List<AgentBasic> result = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
        return result;
    }
}
