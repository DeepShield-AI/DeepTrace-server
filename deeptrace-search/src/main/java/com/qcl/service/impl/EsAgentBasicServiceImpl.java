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

    @Override
    public Page<AgentBasic> search(String keyword, Integer pageNum, Integer pageSize) {
        // 分页请求
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        // keyword 查询
        if (keyword == null || keyword.isEmpty()) {
            return esAgentBasicRepository.findAll(pageRequest);
        }
        return esAgentBasicRepository.findByNameContaining(keyword, pageRequest);
    }
}