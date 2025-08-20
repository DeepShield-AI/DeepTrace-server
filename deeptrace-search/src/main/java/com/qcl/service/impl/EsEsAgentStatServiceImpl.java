package com.qcl.service.impl;

import com.qcl.entity.AgentStat;
import com.qcl.dto.StatItem;
import com.qcl.repository.EsAgentStatRepository;
import com.qcl.service.EsAgentStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EsEsAgentStatServiceImpl implements EsAgentStatService {
    @Autowired
    private EsAgentStatRepository esAgentStatRepository;

    @Override
    public Map<String, List<StatItem>> searchStatItems(String startTime, String endTime, String agentName) {
        Iterable<AgentStat> iterable = esAgentStatRepository.findAll();
        Map<String, List<StatItem>> result = new HashMap<>();
        result.put("cpu", new ArrayList<>());
        result.put("memory", new ArrayList<>());
//        暂时不查询span
//        result.put("span", new ArrayList<>());
        for (AgentStat stat : iterable) {
            if (stat.getTimestamps() == null) continue;
            if (agentName != null && !agentName.equals(stat.getAgentName())) continue;
            List<String> timestamps = stat.getTimestamps();
            List<Float> cpuUsages = stat.getCpuUsages();
            List<Float> memoryUsages = stat.getMemoryUsages();
//            List<Long> spanNums = stat.getSpanNums();
            for (int i = 0; i < timestamps.size(); i++) {
                String ts = timestamps.get(i);
                if (ts.compareTo(startTime) < 0 || ts.compareTo(endTime) > 0) continue;
                if (cpuUsages != null && i < cpuUsages.size())
                    result.get("cpu").add(new StatItem(ts, "cpu", cpuUsages.get(i)));
                if (memoryUsages != null && i < memoryUsages.size())
                    result.get("memory").add(new StatItem(ts, "memory", memoryUsages.get(i)));
//                if (spanNums != null && i < spanNums.size())
//                    result.get("span").add(new StatItem(ts, "span", spanNums.get(i)));
            }
        }
        return result;
    }
}