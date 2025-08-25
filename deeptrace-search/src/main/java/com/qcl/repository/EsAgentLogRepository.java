package com.qcl.repository;

import com.qcl.entity.AgentLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsAgentLogRepository extends ElasticsearchRepository<AgentLog, String> {
    // 模糊查询 agentName 并分页
    Page<AgentLog> findByAgentNameContaining(String keyword, Pageable pageable);
    // lcuuid 查询单条（返回第一条匹配对象，避免数组）
    AgentLog findFirstByLcuuid(String lcuuid);
}