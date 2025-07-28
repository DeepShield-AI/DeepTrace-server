package com.qcl.repository;

import com.qcl.entity.AgentBasic;
import com.qcl.entity.AgentConfig;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsAgentConfigRepository extends ElasticsearchRepository<AgentConfig, String> {
}
