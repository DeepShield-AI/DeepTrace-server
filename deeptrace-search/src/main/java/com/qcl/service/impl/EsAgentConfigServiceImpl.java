package com.qcl.service.impl;

import com.qcl.entity.AgentConfig;
import com.qcl.entity.AgentLog;
import com.qcl.repository.EsAgentConfigRepository;
import com.qcl.service.EsAgentConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EsAgentConfigServiceImpl implements EsAgentConfigService {

    @Autowired
    private EsAgentConfigRepository esAgentConfigRepository;
    @Override
    public List<AgentConfig> search(String keyword, Integer pageNum, Integer pageSize) {
        Iterable<AgentConfig > iterable = esAgentConfigRepository.findAll();
        List<AgentConfig> result = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
        return result;
    }



}
