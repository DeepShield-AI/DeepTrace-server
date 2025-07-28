package com.qcl.service;

import com.qcl.entity.AgentLog;
import com.qcl.entity.AgentStat;

import java.util.List;

public interface EsAgentStatService {

    List<AgentStat> search(String keyword);
}
