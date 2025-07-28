package com.qcl.repository;

import com.qcl.entity.AgentLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsAgentLogRepository extends ElasticsearchRepository<AgentLog, String> {

}
