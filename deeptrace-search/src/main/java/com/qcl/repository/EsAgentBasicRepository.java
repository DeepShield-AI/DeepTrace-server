package com.qcl.repository;

import com.qcl.entity.AgentBasic;
import com.qcl.entity.AgentLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsAgentBasicRepository extends ElasticsearchRepository<AgentBasic, String> {
    // 根据 name 查询，返回一个分页结果（spring-boot-starter-data-elasticsearch 命名规范：findBy+字段名+Containing）
    Page<AgentBasic> findByNameContaining(String keyword, Pageable pageable);
}