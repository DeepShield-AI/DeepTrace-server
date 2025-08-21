package com.qcl.service.impl;

import com.qcl.entity.AgentConfig;
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

    // 全量查询（分页功能，直接返回所有数据）
    @Override
    public List<AgentConfig> search(String keyword, Integer pageNum, Integer pageSize) {
        Iterable<AgentConfig> iterable = esAgentConfigRepository.findAll();
        List<AgentConfig> result = StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
        return result;
    }

    // 根据 agentLcuuid 查询单条详情（值和lcuuid相同）
    @Override
    public AgentConfig findByAgentLcuuid(String agentLcuuid) {
        return esAgentConfigRepository.findById(agentLcuuid).orElse(null);
    }
}