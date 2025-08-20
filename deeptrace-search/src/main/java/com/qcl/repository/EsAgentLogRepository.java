package com.qcl.repository;

import com.qcl.entity.AgentLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsAgentLogRepository extends ElasticsearchRepository<AgentLog, String> {
    Page<AgentLog> findByAgentNameContaining(String keyword, Pageable pageable);
}