package com.qcl.repository;

import com.qcl.entity.AgentBasic;
import com.qcl.entity.AgentConfig;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsAgentConfigRepository extends ElasticsearchRepository<AgentConfig, String> {
}
