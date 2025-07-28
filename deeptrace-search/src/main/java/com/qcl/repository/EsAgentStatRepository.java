package com.qcl.repository;

import com.qcl.entity.AgentStat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface EsAgentStatRepository extends ElasticsearchRepository<AgentStat, String> {
}
