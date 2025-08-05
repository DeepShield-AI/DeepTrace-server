package com.qcl.repository;

import com.qcl.entity.AgentStat;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EsAgentStatRepository extends ElasticsearchRepository<AgentStat, String> {
}
