package com.qcl.service;

import com.qcl.dto.StatItem;
import java.util.List;
import java.util.Map;

public interface EsAgentStatService {
    Map<String, List<StatItem>> searchStatItems(String startTime, String endTime, String agentName);
}