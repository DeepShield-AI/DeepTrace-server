package com.qcl.repository;

import com.qcl.entity.AgentBasic;
import com.qcl.entity.AgentLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsAgentBasicRepository extends ElasticsearchRepository<AgentBasic, String> {
}
