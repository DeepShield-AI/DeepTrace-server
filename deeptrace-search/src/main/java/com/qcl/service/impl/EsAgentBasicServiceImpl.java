package com.qcl.service.impl;

import com.qcl.entity.AgentBasic;
import com.qcl.repository.EsAgentBasicRepository;
import com.qcl.service.EsAgentBasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class EsAgentBasicServiceImpl implements EsAgentBasicService {
    @Autowired
    private EsAgentBasicRepository esAgentBasicRepository;

    // 分页查询
    @Override
    public Page<AgentBasic> search(String keyword, Integer pageNum, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        if (keyword == null || keyword.isEmpty()) {
            return esAgentBasicRepository.findAll(pageRequest);
        }
        return esAgentBasicRepository.findByNameContaining(keyword, pageRequest);
    }

    // 根据 lcuuid 查询单条详情
    @Override
    public AgentBasic findByLcuuid(String lcuuid) {
        return esAgentBasicRepository.findById(lcuuid).orElse(null);
    }
}