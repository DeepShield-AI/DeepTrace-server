package com.qcl.repository;

import com.qcl.entity.AgentBasic;
import com.qcl.entity.AgentLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsAgentBasicRepository extends ElasticsearchRepository<AgentBasic, String> {
    // 根据name字段模糊查询，返回一个分页结果（命名规范：findBy字段名操作
    Page<AgentBasic> findByNameContaining(String keyword, Pageable pageable);
}