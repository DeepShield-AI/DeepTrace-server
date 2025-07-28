package com.qcl.service.impl;

import com.qcl.entity.AgentLog;
import com.qcl.entity.AgentStat;
import com.qcl.repository.EsAgentLogRepository;
import com.qcl.repository.EsAgentStatRepository;
import com.qcl.service.EsAgentStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EsEsAgentStatServiceImpl implements EsAgentStatService {
    @Autowired
    private EsAgentStatRepository esAgentStatRepository;
    @Override
    public List<AgentStat> search(String keyword) {
        Iterable<AgentStat> iterable = esAgentStatRepository.findAll();
        List<AgentStat> result = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
        return result;
    }


}
